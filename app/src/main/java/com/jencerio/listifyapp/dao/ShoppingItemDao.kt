package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items")
    fun getAll(): Flow<List<ShoppingItem>> // Changed to Flow for real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingItem: ShoppingItem)

    @Update
    suspend fun update(shoppingItem: ShoppingItem)

    @Delete
    suspend fun delete(shoppingItem: ShoppingItem)

    @Query("SELECT * FROM shopping_items WHERE shoppingListId = :shoppingListId")
    fun getItemsByShoppingList(shoppingListId: String): Flow<List<ShoppingItem>>
}