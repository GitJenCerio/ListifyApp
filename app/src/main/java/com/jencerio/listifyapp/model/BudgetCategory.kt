package com.jencerio.listifyapp.model


import androidx.room.*

@Entity(tableName = "budget_categories")
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double,
    val isIncome: Boolean
)
