package com.jencerio.listifyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {
    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingList>> get() = _shoppingLists

    private val db = Firebase.firestore

    init {
        viewModelScope.launch {
            repository.getShoppingLists().collect { items ->
                _shoppingLists.value = items
            }
        }
    }

    /** Adds a new shopping list and marks it as PENDING for sync */
    fun addShoppingList(shoppingList: ShoppingList) {
        AuthHelper.getFirebaseToken { token ->
            if (token != null) {
                viewModelScope.launch {
                    val pendingShoppingList = shoppingList.copy(syncStatus = "PENDING")
                    repository.addShoppingList(pendingShoppingList)
                    syncShoppingListPendingItems() // Attempt to sync immediately
                }
            }
        }
    }

    fun updateShoppingItem(shoppingItem: ShoppingList) {
        viewModelScope.launch {
            repository.updateShoppingList(shoppingItem) // Update in Room
            repository.updateShoppingListInFirestore(shoppingItem) // Update in Firestore
        }
    }

    fun deleteShoppingItem(shoppingItem: ShoppingList) {
        viewModelScope.launch {
            repository.deleteShoppingList(shoppingItem) // Delete from Room
            repository.deleteShoppingListFromFirestore(shoppingItem) // Delete from Firestore
        }
    }

    internal fun syncShoppingListPendingItems() {
        viewModelScope.launch {
            val pendingShoppingLists = repository.getPendingShoppingLists()

            for (shoppingList in pendingShoppingLists) {
                try {
                    when (shoppingList.syncStatus) {
                        "PENDING" -> {
                            syncShoppingListsToRemote(shoppingList)
                        }

                        "TO_DELETE" -> {
                            deleteShoppingListFromRemote(shoppingList)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ShoppingListViewModel", "Sync failed for ${shoppingList.id}", e)
                }
            }
        }
    }

    private fun deleteShoppingListFromRemote(list: ShoppingList) {}

    private suspend fun syncShoppingListsToRemote(list: ShoppingList) {
        try {
            db.collection("shopping_lists").document(list.id).set(list).await()
            repository.markAsSynced(list.copy(syncStatus = "SYNCED"))
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to sync item ${list.id}: ${e.localizedMessage}")
        }
    }

    fun softDeleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val updatedShoppingList = shoppingList.copy(isDeleted = true, syncStatus = "PENDING")
            repository.updateShoppingList(updatedShoppingList)
        }
    }
}
