package com.jencerio.listifyapp.localdb


import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jencerio.listifyapp.dao.ShoppingItemDao
import com.jencerio.listifyapp.dao.ShoppingListDao
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.model.ShoppingItem
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class ShoppingListDaoTest {
    private lateinit var shoppingListDao: ShoppingListDao
    private lateinit var shoppingItemDao: ShoppingItemDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        shoppingListDao = db.shoppingListDao()
        shoppingItemDao = db.shoppingItemDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndGetShoppingListWithItems() = runBlocking {
        val userId = "test-user-id"
        val shoppingList = ShoppingList(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = "Grocery List",
            createdAt = Date(),
            updatedAt = Date(),
            isFavorite = false
        )

        shoppingListDao.insert(shoppingList)

        val shoppingItem1 = ShoppingItem(
            id = UUID.randomUUID().toString(),
            shoppingListId = shoppingList.id,
            name = "Apples",
            quantity = 5,
            checked = false,
            createdAt = Date(),
            isInPantry = false
        )

        val shoppingItem2 = ShoppingItem(
            id = UUID.randomUUID().toString(),
            shoppingListId = shoppingList.id,
            name = "Milk",
            quantity = 1,
            checked = true,
            createdAt = Date(),
            isInPantry = false
        )

        shoppingItemDao.insert(shoppingItem1)
        shoppingItemDao.insert(shoppingItem2)

        val allLists = shoppingListDao.getAll().first()
        assertEquals(1, allLists.size)
        assertEquals(shoppingList.title, allLists[0].title)

        val items = shoppingItemDao.getItemsByShoppingList(shoppingList.id).first()

        Log.d("ShoppingListDaoTest", "Printing something: $items")

        assertEquals(2, items.size)
        assertTrue(items.any { it.name == "Apples" })
        assertTrue(items.any { it.name == "Milk" })
    }
}