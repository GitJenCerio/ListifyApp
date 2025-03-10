package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.ShoppingItemDao
import com.jencerio.listifyapp.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

class ShoppingItemRepository(private val shoppingItemDao: ShoppingItemDao) {

    val shoppingItems: Flow<List<ShoppingItem>> = shoppingItemDao.getAll()

    suspend fun addShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.insert(shoppingItem)
    }

    suspend fun updateShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.update(shoppingItem)
    }

    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        shoppingItemDao.delete(shoppingItem)
    }
}
