package com.jencerio.listifyapp.model


import androidx.room.*

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: String,
    val userId: String,
    val category: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean
)
