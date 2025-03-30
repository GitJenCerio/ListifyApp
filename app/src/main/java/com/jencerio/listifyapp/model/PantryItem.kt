package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "pantry_items")
data class PantryItem(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val imageURL: String,
    val quantity: Int,
    val addedAt: Date,
    val expirationDate: Date?,
    val fromShoppingListId: String?
)