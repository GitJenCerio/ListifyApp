package com.jencerio.listifyapp.repository

import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    val budgetItems: Flow<List<Budget>> = budgetDao.getAll()

    suspend fun addBudgetItem(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun updateBudgetItem(budget: Budget) {
        budgetDao.update(budget)
    }

    suspend fun deleteBudgetItem(budget: Budget) {
        budgetDao.delete(budget)
    }
}
