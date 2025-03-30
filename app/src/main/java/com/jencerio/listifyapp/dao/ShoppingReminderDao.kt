package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.ShoppingReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingReminderDao {
    @Query("SELECT * FROM shopping_reminders")
    fun getAll(): Flow<List<ShoppingReminder>> // For real-time updates

    @Query("SELECT * FROM shopping_reminders")
    suspend fun getAllAsList(): List<ShoppingReminder> // For sync operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingReminder: ShoppingReminder)

    @Update
    suspend fun update(shoppingReminder: ShoppingReminder)

    @Delete
    suspend fun delete(shoppingReminder: ShoppingReminder)

    @Query("SELECT * FROM shopping_reminders WHERE id = :id")
    suspend fun getById(id: String): ShoppingReminder?

    @Query("SELECT * FROM shopping_reminders WHERE userId = :userId")
    fun getByUserId(userId: String): Flow<List<ShoppingReminder>>
}