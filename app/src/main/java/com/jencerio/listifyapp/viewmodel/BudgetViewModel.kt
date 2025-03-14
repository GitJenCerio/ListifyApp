package com.jencerio.listifyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        syncPendingItems() // Sync pending items on app start
    }

    fun addBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val pendingBudget = budget.copy(syncStatus = "PENDING")
            repository.addBudgetItem(pendingBudget)
            syncPendingItems() // Try to sync in the background
        }
    }

    fun updateBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val pendingBudget = budget.copy(syncStatus = "PENDING")
            repository.updateBudgetItem(pendingBudget)
            syncPendingItems()
        }
    }

    fun deleteBudgetItem(budget: Budget) {
        viewModelScope.launch {
            val deletingBudget = budget.copy(syncStatus = "DELETING")
            repository.updateBudgetItem(deletingBudget)

            db.collection("budgets").document(budget.id).delete()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        repository.deleteBudgetItem(budget)
                    }
                }
                .addOnFailureListener {
                    Log.e("BudgetViewModel", "Error deleting item from Firestore")
                }
        }
    }


    internal fun syncPendingItems() {
        viewModelScope.launch {
            val pendingItems = repository.getPendingBudgetItems()
            pendingItems.forEach { budget ->
                try {
                    db.collection("budgets").document(budget.id).set(budget).await()
                    repository.markAsSynced(budget.copy(syncStatus = "SYNCED"))
                } catch (e: Exception) {
                    Log.e("BudgetViewModel", "Sync failed for ${budget.id}", e)
                }
            }
        }
    }

    fun markItemForDeletion(budget: Budget) {
        viewModelScope.launch {
            val deletingBudget = budget.copy(syncStatus = "DELETING")
            repository.updateBudgetItem(deletingBudget)
        }
    }


}
