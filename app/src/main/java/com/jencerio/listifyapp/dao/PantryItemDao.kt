package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.PantryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryItemDao {
    @Query("SELECT * FROM pantry_items")
    fun getAll(): Flow<List<PantryItem>> // Changed to Flow for real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pantryItem: PantryItem)

    @Update
    suspend fun update(pantryItem: PantryItem)

    @Delete
    suspend fun delete(pantryItem: PantryItem)
}