package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String,
    val userId: String,
    val category: String,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val syncStatus: String = "PENDING", // NEW FIELD (PENDING / SYNCED)
    val isSynced: Boolean
) {
    constructor() : this("", "", "", "", 0.0, false, isSynced = false)
}