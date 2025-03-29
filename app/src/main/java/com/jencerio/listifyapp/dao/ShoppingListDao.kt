package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists")
    fun getAll(): Flow<List<ShoppingList>> // Changed to Flow for real-time updates

    @Query("SELECT * FROM shopping_lists WHERE id = :id LIMIT 1")
    suspend fun getShoppingListById(id: String): ShoppingList?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingList)

    @Update
    suspend fun update(shoppingList: ShoppingList)

    @Delete
    suspend fun delete(shoppingList: ShoppingList)


    @Query("UPDATE shopping_lists SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM shopping_lists WHERE syncStatus = 'PENDING'")
    suspend fun getPendingShoppingLists(): List<ShoppingList>

    @Query("UPDATE shopping_lists SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("SELECT * FROM shopping_lists WHERE isDeleted = 1")
    suspend fun getDeletedShoppingLists(): List<ShoppingList>

}