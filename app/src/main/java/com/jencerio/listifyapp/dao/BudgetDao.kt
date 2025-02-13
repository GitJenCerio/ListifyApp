package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.BudgetCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_categories")
    fun getAll(): Flow<List<BudgetCategory>> // Changed to Flow for real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetCategory: BudgetCategory)

    @Update
    suspend fun update(budgetCategory: BudgetCategory)

    @Delete
    suspend fun delete(budgetCategory: BudgetCategory)
}