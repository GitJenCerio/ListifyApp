package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.ShoppingReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingReminderDao {
    @Query("SELECT * FROM shopping_reminders")
    fun getAll(): Flow<List<ShoppingReminder>> // Changed to Flow for real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingReminder: ShoppingReminder)

    @Update
    suspend fun update(shoppingReminder: ShoppingReminder)

    @Delete
    suspend fun delete(shoppingReminder: ShoppingReminder)
}