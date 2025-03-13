package com.jencerio.listifyapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao?) { // 🔹 Make it optional
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("shopping_lists")

    fun getShoppingLists(): Flow<List<ShoppingList>> {
        return shoppingListDao?.getAll() ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun addShoppingList(shoppingList: ShoppingList) {
        shoppingListDao?.insert(shoppingList) ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun updateShoppingList(shoppingList: ShoppingList) {
        shoppingListDao?.update(shoppingList) ?: throw IllegalStateException("Local DB not initialized")
    }

    suspend fun deleteShoppingList(shoppingList: ShoppingList) {
        shoppingListDao?.delete(shoppingList) ?: throw IllegalStateException("Local DB not initialized")
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
}
