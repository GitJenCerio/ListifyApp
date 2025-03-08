package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "shopping_lists")
data class ShoppingList(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val createdAt: Date,
    val updatedAt: Date,
    val isFavorite: Boolean
)

