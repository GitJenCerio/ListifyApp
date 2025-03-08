package com.jencerio.listifyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.dao.UserDao
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.Users

@Database(entities = [Users::class, Budget::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
