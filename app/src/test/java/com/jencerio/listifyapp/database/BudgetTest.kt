package com.jencerio.listifyapp.database

import com.jencerio.listifyapp.model.Budget
import org.junit.Assert.*
import org.junit.Test

class BudgetTest {

    @Test
    fun `test budget model instantiation`() {
        val budget = Budget(
            id = "123",
            userId = "user123",
            category = "Food",
            description = "Groceries",
            amount = 100.0,
            isIncome = false,
            isSynced = false
        )

        assertEquals("123", budget.id)
        assertEquals("user123", budget.userId)
        assertEquals("Food", budget.category)
        assertEquals("Groceries", budget.description)
        assertEquals(100.0, budget.amount, 0.01)
        assertFalse(budget.isIncome)
    }
}
