package com.jencerio.listifyapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class BudgetRepository(private val budgetDao: BudgetDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("budget")


    val budgetItems: Flow<List<Budget>> = budgetDao.getAll()

    suspend fun addBudgetItem(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun updateBudgetItem(budget: Budget) {
        budgetDao.update(budget)
    }

    suspend fun deleteBudgetItem(budget: Budget) {
        budgetDao.delete(budget)
    }


    suspend fun addBudgetItemToFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).set(shoppingList).await()
    }

    suspend fun updateBudgetItemInFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).set(shoppingList).await()
    }

    suspend fun deleteBudgetItemFromFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).delete().await()
    }

    suspend fun getBudgetItemFromFirestore(id: String): ShoppingList? {
        val documentSnapshot = collectionRef.document(id).get().await()
        return documentSnapshot.toObject(ShoppingList::class.java)
    }

    suspend fun getPendingBudgetItems(): List<Budget> { // NEW METHOD
        return budgetDao.getPendingBudgetItems()
    }

    suspend fun markAsSynced(budget: Budget) { // NEW METHOD
        budgetDao.markAsSynced(budget.id)
    }

    suspend fun syncPendingBudgetItems() {
        val pendingItems = getPendingBudgetItems()
        for (budget in pendingItems) {
            try {
                firestore.collection("budgets").document(budget.id).set(budget).await()
                budgetDao.markAsSynced(budget.id) // Mark as synced after success
            } catch (e: Exception) {
                e.printStackTrace() // Handle errors (e.g., log them)
            }
        }
    }
}
