package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.model.BudgetCategory
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetCategoryDao: BudgetDao) {

    val budgetItems: Flow<List<BudgetCategory>> = budgetCategoryDao.getAll()

    suspend fun addBudgetItem(budgetCategory: BudgetCategory) {
        budgetCategoryDao.insert(budgetCategory)
    }

    suspend fun updateBudgetItem(budgetCategory: BudgetCategory) {
        budgetCategoryDao.update(budgetCategory)
    }

    suspend fun deleteBudgetItem(budgetCategory: BudgetCategory) {
        budgetCategoryDao.delete(budgetCategory)
    }
}
