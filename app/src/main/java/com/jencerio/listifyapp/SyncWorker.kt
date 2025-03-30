package com.jencerio.listifyapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.repository.BudgetRepository
import com.jencerio.listifyapp.repository.ShoppingListRepository
import com.jencerio.listifyapp.viewmodel.BudgetViewModel
import com.jencerio.listifyapp.viewmodel.ShoppingListViewModel

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)

        val budgetRepository = BudgetRepository(database.budgetDao())
        val shoppingListRepository = ShoppingListRepository(database.shoppingListDao())

        val budgetViewModel = BudgetViewModel(budgetRepository)
        val shoppingListViewModel = ShoppingListViewModel(shoppingListRepository)

        return try {
            budgetViewModel.syncBudgetPendingItems() // Sync budget data
            shoppingListViewModel.syncShoppingListPendingItems() // Sync shopping list data
            Result.success()
        } catch (e: Exception) {
            Result.retry() // Retry if sync fails
        }
    }
}
