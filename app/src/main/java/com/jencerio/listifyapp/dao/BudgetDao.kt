package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget")
    fun getAll(): Flow<List<Budget>> // Changed to Flow for real-time updates

    @Query("SELECT * FROM budget WHERE id = :budgetId LIMIT 1")
    suspend fun getBudgetById(budgetId: String): Budget? // Fetch budget by ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}