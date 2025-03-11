package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jencerio.listifyapp.utils.Converters
import java.util.Date


@Entity(tableName = "shopping_lists")
@TypeConverters(Converters::class)
data class ShoppingList(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val createdAt: Date,
    val updatedAt: Date,
    val isFavorite: Boolean
)

