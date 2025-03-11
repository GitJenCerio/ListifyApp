package com.jencerio.listifyapp

import com.jencerio.listifyapp.database.AppDatabase


import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jencerio.listifyapp.dao.BudgetDao
import com.jencerio.listifyapp.model.Budget
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class BudgetDaoTest {
    private lateinit var budgetDao: BudgetDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        budgetDao = db.budgetDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetBudgetItem() = runBlocking {
        // Create test budget item
        val userId = "test-user-id"
        val budgetItem = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Groceries",
            description = "Weekly shopping",
            amount = 50.0,
            isIncome = false
        )

        // Insert item
        budgetDao.insert(budgetItem)

        // Get all items and verify
        val allItems = budgetDao.getAll().first()
        assertEquals(1, allItems.size)
        assertEquals(budgetItem.category, allItems[0].category)
        assertEquals(budgetItem.amount, allItems[0].amount, 0.0)
    }

    @Test
    fun testUpdateBudgetItem() = runBlocking {
        // Create and insert test budget item
        val userId = "test-user-id"
        val budgetItem = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Groceries",
            description = "Weekly shopping",
            amount = 50.0,
            isIncome = false
        )
        budgetDao.insert(budgetItem)

        // Update the item
        val updatedItem = budgetItem.copy(
            amount = 75.0,
            description = "Monthly shopping"
        )
        budgetDao.update(updatedItem)

        // Get the item and verify it was updated
        val allItems = budgetDao.getAll().first()
        assertEquals(1, allItems.size)
        assertEquals(updatedItem.description, allItems[0].description)
        assertEquals(75.0, allItems[0].amount, 0.0)
    }

    @Test
    fun testDeleteBudgetItem() = runBlocking {
        // Create and insert test budget item
        val userId = "test-user-id"
        val budgetItem = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Groceries",
            description = "Weekly shopping",
            amount = 50.0,
            isIncome = false
        )
        budgetDao.insert(budgetItem)

        // Verify item exists
        var allItems = budgetDao.getAll().first()
        assertEquals(1, allItems.size)

        // Delete the item
        budgetDao.delete(budgetItem)

        // Verify item was deleted
        allItems = budgetDao.getAll().first()
        assertTrue(allItems.isEmpty())
    }

    @Test
    fun testGetAllBudgets() = runBlocking {
        // Create test user ID
        val userId = "test-user-id"

        // Create multiple budget items
        val budgetItem1 = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Groceries",
            description = "Weekly shopping",
            amount = 50.0,
            isIncome = false
        )

        val budgetItem2 = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Entertainment",
            description = "Movie night",
            amount = 30.0,
            isIncome = false
        )

        val budgetItem3 = Budget(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = "Salary",
            description = "Monthly income",
            amount = 2000.0,
            isIncome = true
        )

        // Insert items
        budgetDao.insert(budgetItem1)
        budgetDao.insert(budgetItem2)
        budgetDao.insert(budgetItem3)

        // Get all items and verify
        val allItems = budgetDao.getAll().first()
        assertEquals(3, allItems.size)

        // Verify each item
        assertEquals(budgetItem1.category, allItems[0].category)
        assertEquals(budgetItem1.amount, allItems[0].amount, 0.0)

        assertEquals(budgetItem2.category, allItems[1].category)
        assertEquals(budgetItem2.amount, allItems[1].amount, 0.0)

        assertEquals(budgetItem3.category, allItems[2].category)
        assertEquals(budgetItem3.amount, allItems[2].amount, 0.0)
    }
}