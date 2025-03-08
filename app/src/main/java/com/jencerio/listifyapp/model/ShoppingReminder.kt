package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.Date

@Entity(tableName = "shopping_reminders")
data class ShoppingReminder(
    @PrimaryKey val id: String,
    val userId: String,
    val shoppingListId: String?,
    val reminderDate: Date,
    val message: String
)
