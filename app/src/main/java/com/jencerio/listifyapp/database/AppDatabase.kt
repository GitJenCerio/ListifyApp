package com.jencerio.listifyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.dao.ShoppingItemDao
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.dao.UserDao
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingItem
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.model.Users
import com.jencerio.listifyapp.utils.Converters

@Database(entities = [Users::class, Budget::class, ShoppingList::class, ShoppingItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao

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