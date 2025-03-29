package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.firestore.PropertyName
import com.jencerio.listifyapp.utils.Converters
import java.util.Date


@Entity(tableName = "shopping_lists")
@TypeConverters(Converters::class)
data class ShoppingList(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val title: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isFavorite: Boolean = false,
    val syncStatus: String = "PENDING",
    @PropertyName("synced")
    val isSynced: Boolean,
    val isDeleted: Boolean = false
) {
    constructor() : this("", "", "", Date(), Date(), false, "PENDING", false, false)
}
