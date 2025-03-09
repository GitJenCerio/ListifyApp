package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val shoppingLists: Flow<List<ShoppingList>> = shoppingListDao.getAll()

    suspend fun addShoppingList(shoppingItem: ShoppingList) {
        shoppingListDao.insert(shoppingItem)
    }

    suspend fun updateShoppingList(shoppingItem: ShoppingList) {
        shoppingListDao.update(shoppingItem)
    }

    suspend fun deleteShoppingList(shoppingItem: ShoppingList) {
        shoppingListDao.delete(shoppingItem)
    }
}
