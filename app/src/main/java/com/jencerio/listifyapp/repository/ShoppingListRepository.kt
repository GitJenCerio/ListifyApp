package com.jencerio.listifyapp.repository

import android.util.Log
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao?) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("shopping_lists")


    val shoppingList: Flow<List<ShoppingList>> = shoppingListDao?.getAll()!!


    suspend fun addShoppingList(shoppingList: ShoppingList) {
        shoppingListDao?.insert(shoppingList)
            ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun updateShoppingList(shoppingList: ShoppingList) {
        shoppingListDao?.update(shoppingList)
            ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun deleteShoppingList(shoppingList: ShoppingList) {
        Log.d("deleteShoppingList", "I'm here : ${shoppingList.toString()}")

        if (shoppingList.syncStatus == "TO_DELETE") {
            shoppingListDao?.delete(shoppingList)
        } else {
            shoppingListDao?.update(shoppingList)
        }
    }

    // 🔥 Firebase Firestore CRUD Methods 🔥

    suspend fun addShoppingListToFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).set(shoppingList).await()
    }

    suspend fun updateShoppingListInFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).set(shoppingList).await()
    }

    suspend fun deleteShoppingListFromFirestore(shoppingList: ShoppingList) {
        collectionRef.document(shoppingList.id).delete().await()
    }

    suspend fun getShoppingListFromFirestore(id: String): ShoppingList? {
        val documentSnapshot = collectionRef.document(id).get().await()
        return documentSnapshot.toObject(ShoppingList::class.java)
    }

    suspend fun getPendingShoppingLists(): List<ShoppingList> {
        return shoppingListDao?.getPendingShoppingLists()
            ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun markAsSynced(shoppingList: ShoppingList) {
        shoppingListDao?.markAsSynced(shoppingList.id)
    }

    suspend fun syncPendingShoppingLists() {
        val pendingShoppingLists = getPendingShoppingLists()
        for (shoppingList in pendingShoppingLists) {
            try {
                firestore.collection("shopping_lists").document(shoppingList.id).set(shoppingList)
                    .await()
                shoppingListDao?.markAsSynced(shoppingList.id) // Mark as synced after success
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getShoppingListById(id: String): ShoppingList? {
        return shoppingListDao?.getShoppingListById(id)
    }

    // Get active lists from local DB
    suspend fun getActiveShoppingLists(): List<ShoppingList> {
        return shoppingListDao?.getActiveLists() ?: emptyList()
    }

    // Cleanup deleted items in local DB
    suspend fun cleanupDeletedShoppingLists() {
        shoppingListDao?.cleanupDeleted()
    }

    // Fetch from Firestore (including deleted items)
    suspend fun getShoppingListsFromFirestore(userId: String): List<ShoppingList> {
        return try {
            firestore.collection("shoppingLists")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(ShoppingList::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Upsert with proper Firestore mapping
    suspend fun upsertShoppingList(list: ShoppingList) {
        // Convert Firestore's 'deleted' to local 'isDeleted'
        val localList = list.copy(isDeleted = list.isDeleted)
        shoppingListDao?.upsertShoppingList(localList)
    }

    // Hard delete implementation
    suspend fun hardDeleteShoppingList(list: ShoppingList) {
        shoppingListDao?.hardDeleteShoppingList(list.id)
    }
}
