package com.jencerio.listifyapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreRepository(
    private val budgetDao: BudgetDao,
    private val shoppingListDao: ShoppingListDao
) {
    private val db = FirebaseFirestore.getInstance()

    // Fetch all budgets for the current user
    suspend fun syncBudgetsFromFirestore() {
        withContext(Dispatchers.IO) {
            try {
                val userId = AuthHelper.getCurrentUserId()
                if (userId != null) {
                    val snapshot = db.collection("budget")
                        .whereEqualTo("userId", userId) // Fetch only budgets of the logged-in user
                        .get()
                        .await()

                    val budgets = snapshot.documents.mapNotNull { it.toObject(Budget::class.java) }
                    budgetDao.insertAll(budgets) // Store in Room
                }
            } catch (e: Exception) {
                println("Firestore sync failed: ${e.message}")
            }
        }
    }

    // Fetch a single budget by ID from Firestore
    suspend fun getBudgetFromFirestore(budgetId: String): Budget? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = AuthHelper.getCurrentUserId()
                if (userId != null) {
                    val document = db.collection("budget")
                        .document(budgetId)
                        .get()
                        .await()

                    val budget = document.toObject(Budget::class.java)
                    if (budget?.userId == userId) { // Ensure it's the user's budget
                        return@withContext budget
                    }
                }
                null
            } catch (e: Exception) {
                println("Failed to fetch budget: ${e.message}")
                null
            }
        }
    }
}
