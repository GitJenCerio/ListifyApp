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
    @PropertyName("income") // Map Firestore field "income" to "isIncome"
    val isIncome: Boolean,
    val syncStatus: String = "PENDING", // NEW FIELD (PENDING / SYNCED or TO_DELETE)
    @PropertyName("synced") // Map Firestore field "synced" to "isSynced"
    val isSynced: Boolean
) {
    // Add a no-argument constructor for Firestore
    constructor() : this("", "", "", "", 0.0, false, "PENDING", false)
}