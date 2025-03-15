package com.jencerio.listifyapp.dao

import android.util.Log
import androidx.room.*
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isDeleted = 0")
    fun getAll(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE id = :budgetId LIMIT 1")
    suspend fun getBudgetById(budgetId: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget) {
        Log.d("BudgetDao", "Deleting budget from local DB: ${budget.id}")
    }

    @Query("UPDATE budgets SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT * FROM budgets WHERE syncStatus = 'PENDING'")
    suspend fun getPendingBudgetItems(): List<Budget>

    // Soft delete: instead of deleting, mark as deleted
    @Query("UPDATE budgets SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String)

    // Retrieve all items marked for deletion (for syncing before hard delete)
    @Query("SELECT * FROM budgets WHERE isDeleted = 1")
    suspend fun getDeletedBudgets(): List<Budget>
}
