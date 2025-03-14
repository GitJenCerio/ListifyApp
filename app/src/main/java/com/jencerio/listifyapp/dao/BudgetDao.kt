package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAll(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :budgetId LIMIT 1")
    suspend fun getBudgetById(budgetId: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("UPDATE budgets SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM budgets WHERE syncStatus = 'PENDING'")
    suspend fun getPendingBudgetItems(): List<Budget>
}
