package com.jencerio.listifyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {
    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingList>> get() = _shoppingLists

    private val db = Firebase.firestore

    init {
        viewModelScope.launch {
            repository.shoppingList.collect { items ->
                _shoppingLists.value = items
            }
        }

        syncShoppingListPendingItems()
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

    fun updateShoppingList(shoppingItem: ShoppingList) {
        viewModelScope.launch {
            val pendingShoppingList = shoppingItem.copy(syncStatus = "PENDING")
            repository.updateShoppingList(pendingShoppingList)
            syncShoppingListPendingItems()
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val shoppingList = shoppingList.copy(syncStatus = "TO_DELETE")
            repository.deleteShoppingList(shoppingList)
            syncShoppingListPendingItems()
        }
    }

    internal fun syncShoppingListPendingItems() {
        viewModelScope.launch {
            val userId = AuthHelper.getCurrentUserId() ?: return@launch

            // 1. Upload pending local changes to Firestore first
            val pendingItems = repository.getPendingShoppingLists()
            for (shoppingList in pendingItems) {
                try {
                    when (shoppingList.syncStatus) {
                        "PENDING" -> {
                            syncShoppingListsToRemote(shoppingList)
                            // Mark as synced after successful upload
                            repository.upsertShoppingList(shoppingList.copy(isSynced = true))
                        }
                        "TO_DELETE" -> {
                            deleteShoppingListFromRemote(shoppingList)
                            // Only delete locally if Firestore deletion succeeds
                            repository.hardDeleteShoppingList(shoppingList)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SyncError", "Failed to sync ${shoppingList.id}", e)
                    // Keep as pending if sync fails
                    repository.upsertShoppingList(shoppingList.copy(isSynced = false))
                }
            }

            // 2. Fetch and merge Firestore data
            val remoteLists = repository.getShoppingListsFromFirestore(userId)

            // 3. Update local database with Firestore state
            remoteLists.forEach { remoteList ->
                val localList = repository.getShoppingListById(remoteList.id)

                if (remoteList.isDeleted) {
                    // If marked deleted in Firestore, delete locally
                    if (localList != null) {
                        repository.hardDeleteShoppingList(localList)
                    }
                } else {
                    // Update or insert with Firestore data
                    val mergedList = localList?.let {
                        it.copy(
                            title = remoteList.title,
                            isDeleted = remoteList.isDeleted, // Map Firestore deleted to local isDeleted
                            syncStatus = if (it.isSynced) "SYNCED" else "PENDING",
                            updatedAt = maxOf(it.updatedAt, remoteList.updatedAt)
                        )
                    } ?: remoteList.copy(
                        syncStatus = "SYNCED",
                        isSynced = true,
                        isDeleted = remoteList.isDeleted
                    )

                    repository.upsertShoppingList(mergedList)
                }
            }

            // 4. Clean up any remaining local items marked as deleted
            repository.cleanupDeletedShoppingLists()

            // Update UI state
            _shoppingLists.value = repository.getActiveShoppingLists()
        }
    }

    private suspend fun fetchRemoteShoppingLists() {
        try {
            val userId = AuthHelper.getCurrentUserId()
            if (userId == null) {
                Log.e("ShoppingListViewModel", "User not authenticated")
                return
            }

            val snapshot = db.collection("shopping_lists")
                .whereEqualTo("userId", userId)
                .get().await()

            val remoteShoppingLists = snapshot.documents.mapNotNull {  it.toObject(ShoppingList::class.java) }

            val filteredShoppingLists = remoteShoppingLists.filter { it.syncStatus != "TO_DELETE" }

            for (remoteShoppingList in filteredShoppingLists) {
                val shoppingListFromLocal = repository.getShoppingListById(remoteShoppingList.id)

                if (shoppingListFromLocal == null || shoppingListFromLocal.syncStatus != "SYNCED") {
                    repository.addShoppingList(remoteShoppingList.copy(syncStatus = "SYNCED"))
                }
            }

            Log.d("ShoppingListViewModel", "Fetched ${filteredShoppingLists.size} shopping list for user $userId")
        } catch (e: Exception) {
            Log.e("ShoppingListViewModel", "Failed to fetch shopping list: ${e.localizedMessage}")
        }
    }

    private suspend fun deleteShoppingListFromRemote(shoppingList: ShoppingList) {
        try {
            Log.d("ShoppingListViewModel", "Deleting from Firestore: ${shoppingList.id}")
            db.collection("shopping_lists").document(shoppingList.id).delete().await()
            Log.d("ShoppingListViewModel", "Successfully deleted from Firestore: ${shoppingList.id}")

            repository.deleteShoppingList(shoppingList) // Delete from local DB after remote success

            // Small delay to allow Firestore to process the deletion
            kotlinx.coroutines.delay(1000) // Wait for 1 second

            fetchRemoteShoppingLists() // Now fetch updated budgets

            Log.d("BudgetViewModel", "Budget has been removed $shoppingList")
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to delete item ${shoppingList.id}: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e("BudgetViewModel", "Failed to delete item ${shoppingList.id}: ${e.localizedMessage}")
        }
        finally {
            Log.d("BudgetViewModel", "Budget has been removed $shoppingList")
        }
    }

    private suspend fun syncShoppingListsToRemote(list: ShoppingList) {
        try {
            db.collection("shopping_lists").document(list.id).set(list).await()
            repository.markAsSynced(list.copy(syncStatus = "SYNCED"))
        } catch (e: Exception) {
            Log.e("ShoppingListViewModel", "Failed to sync item ${list.id}: ${e.localizedMessage}")
        }
    }

    fun softDeleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val updatedBudget = shoppingList.copy(isDeleted = true, syncStatus = "PENDING")
            repository.updateShoppingList(updatedBudget)
        }
    }
}
