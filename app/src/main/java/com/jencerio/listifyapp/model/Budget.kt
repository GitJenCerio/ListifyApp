package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String,
    val userId: String,
    val category: String,
    val description: String,
    val amount: Double,
    @PropertyName("income")
    val isIncome: Boolean,
    val syncStatus: String = "PENDING",
    @PropertyName("synced")
    val isSynced: Boolean,
    val isDeleted: Boolean = false // NEW FIELD for soft deletion
) {
    constructor() : this("", "", "", "", 0.0, false, "PENDING", false, false)
}