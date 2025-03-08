package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey val id: String,
    val shoppingListId: String,
    val name: String,
    val quantity: Int,
    val checked: Boolean,
    val createdAt: Date,
    val isInPantry: Boolean
)