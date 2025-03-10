package com.jencerio.listifyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters  // Import TypeConverters
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.dao.UserDao
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingList  // Ensure ShoppingList is imported
import com.jencerio.listifyapp.model.Users

@Database(entities = [Users::class, Budget::class, ShoppingList::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  // Ensure this matches your Converters file
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
