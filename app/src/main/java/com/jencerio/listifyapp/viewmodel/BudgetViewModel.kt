package com.jencerio.listifyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {
    private val _budgetItems = MutableStateFlow<List<Budget>>(emptyList())
    val budgetItems: StateFlow<List<Budget>> get() = _budgetItems

    private val db = Firebase.firestore // Firestore instance

    init {
        viewModelScope.launch {
            repository.budgetItems.collect { items ->
                _budgetItems.value = items
            }
        }
        syncBudgetPendingItems() // Sync pending items on app start
    }

    /** Adds a new budget item and marks it as PENDING for sync */
    fun addBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val pendingBudget = budget.copy(syncStatus = "PENDING")
            repository.addBudgetItem(pendingBudget)
            syncBudgetPendingItems() // Attempt to sync immediately
        }
    }

    /** Updates a budget item and marks it as PENDING for sync */
    fun updateBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val pendingBudget = budget.copy(syncStatus = "PENDING")
            repository.updateBudgetItem(pendingBudget)
            syncBudgetPendingItems()
        }
    }

    /** Marks an item for deletion (soft delete) */
    fun deleteBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val pendingBudget = budget.copy(syncStatus = "TO_DELETE") // Corrected sync status
            repository.deleteBudgetItem(pendingBudget)
            syncBudgetPendingItems() // Attempt to sync immediately
        }
    }

    /** Syncs all pending items (new, updated, and deleted) */
    internal fun syncBudgetPendingItems() {
        viewModelScope.launch {
            val pendingItems = repository.getPendingBudgetItems()

            // 1. Upload pending local changes to Firestore
            for (budget in pendingItems) {
                var counter = 0
                Log.d("COUNTER", "$counter")
                try {
                    when (budget.syncStatus) {
                        "PENDING" -> {
                            syncBudgetsToRemote(budget)
                            Log.d("BudgetViewModel", "PENDING Sync successful for ${budget.id}")
                            counter++
                        }
                        "TO_DELETE" -> {
                            deleteBudgetFromRemote(budget)
                            Log.d("BudgetViewModel", "TO_DELETE Sync successful for ${budget.id}")
                            counter++
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BudgetViewModel", "Sync failed for ${budget.id}", e)
                }
            }

            fetchRemoteBudgets()

            _budgetItems.value = repository.budgetItems.first()
        }
    }

    private suspend fun fetchRemoteBudgets() {
        try {
            val userId = AuthHelper.getCurrentUserId()
            if (userId == null) {
                Log.e("BudgetViewModel", "User not authenticated")
                return
            }

            val snapshot = db.collection("budgets")
                .whereEqualTo("userId", userId)
                .get().await()

            val remoteBudgets = snapshot.documents.mapNotNull { it.toObject(Budget::class.java) }

            // Filter out budgets that were marked for deletion locally
            val filteredBudgets = remoteBudgets.filter { it.syncStatus != "TO_DELETE" }

            for (remoteBudget in filteredBudgets) {
                val localBudget = repository.getBudgetById(remoteBudget.id)

                if (localBudget == null || localBudget.syncStatus != "SYNCED") {
                    repository.addBudgetItem(remoteBudget.copy(syncStatus = "SYNCED"))
                }
            }

            Log.d("BudgetViewModel", "Fetched ${filteredBudgets.size} budget items for user $userId")
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to fetch budgets: ${e.localizedMessage}")
        }
    }




    /** Uploads pending budget items to Firestore */
    private suspend fun syncBudgetsToRemote(budget: Budget) {
        try {
            db.collection("budgets").document(budget.id).set(budget).await()
            repository.markAsSynced(budget.copy(syncStatus = "SYNCED"))
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to sync item ${budget.id}: ${e.localizedMessage}")
        }
    }

    /** Deletes a budget item from Firestore and then removes it from Room */
    private suspend fun deleteBudgetFromRemote(budget: Budget) {
        try {
            Log.d("BudgetViewModel", "Deleting from Firestore: ${budget.id}")
            db.collection("budgets").document(budget.id).delete().await()
            Log.d("BudgetViewModel", "Successfully deleted from Firestore: ${budget.id}")

            repository.deleteBudgetItem(budget) // Delete from local DB after remote success

            // Small delay to allow Firestore to process the deletion
            kotlinx.coroutines.delay(1000) // Wait for 1 second

            fetchRemoteBudgets() // Now fetch updated budgets
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to delete item ${budget.id}: ${e.localizedMessage}")
        }
        finally {
            Log.d("BudgetViewModel", "Budget has been removed $budget")
        }
    }

    fun softDeleteBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val updatedBudget = budget.copy(isDeleted = true, syncStatus = "PENDING") // Mark for deletion and sync
            repository.updateBudgetItem(updatedBudget)
        }
    }

}
