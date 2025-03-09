package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.PantryItemDao
import com.jencerio.listifyapp.model.PantryItem
import kotlinx.coroutines.flow.Flow

class PantryItemRepository(private val pantryItemDao: PantryItemDao) {

    val pantryItems: Flow<List<PantryItem>> = pantryItemDao.getAll()

    suspend fun addPantryItem(pantryItem: PantryItem) {
        pantryItemDao.insert(pantryItem)
    }

    suspend fun updatePantryItem(pantryItem: PantryItem) {
        pantryItemDao.update(pantryItem)
    }

    suspend fun deletePantryItem(pantryItem: PantryItem) {
        pantryItemDao.delete(pantryItem)
    }
}
