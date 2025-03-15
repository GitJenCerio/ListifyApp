package com.jencerio.listifyapp


import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.repository.BudgetRepository
import com.jencerio.listifyapp.viewmodel.BudgetViewModel

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val repository = BudgetRepository(AppDatabase.getDatabase(applicationContext).budgetDao())
        val viewModel = BudgetViewModel(repository)

        return try {
            viewModel.syncBudgetPendingItems() // Sync data when internet is available
            Result.success()
        } catch (e: Exception) {
            Result.retry() // Retry if sync fails
        }
    }
}


