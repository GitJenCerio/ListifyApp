package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.ShoppingItemDao
import com.jencerio.listifyapp.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

class ShoppingItemRepository(private val shoppingItemDao: ShoppingItemDao) {

    val shoppingItems: Flow<List<ShoppingItem>> = shoppingItemDao.getAll()

    suspend fun addPantryItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.insert(shoppingItem)
    }

    suspend fun updatePantryItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.update(shoppingItem)
    }

    suspend fun deletePantryItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.delete(shoppingItem)
    }
}
