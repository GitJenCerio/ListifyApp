package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jencerio.listifyapp.utils.Converters
import java.util.Date


@Entity(tableName = "shopping_reminders")
@TypeConverters(Converters::class)
data class ShoppingReminder(
    @PrimaryKey val id: String,
    val userId: String,
    val shoppingListId: String?,
    val reminderDate: Date, // Room will now use the TypeConverter
    val message: String
) {
    constructor() : this("", "", null, Date(), "")
}