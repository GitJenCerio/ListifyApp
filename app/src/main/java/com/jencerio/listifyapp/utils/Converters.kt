package com.jencerio.listifyapp.utils

import androidx.room.TypeConverter
import com.google.type.Date
import java.util.Date as UtilDate

object Converters {
    // Convert from com.google.type.Date to String
    @TypeConverter
    fun fromGoogleDate(date: Date?): String? {
        return date?.let { "${it.year}-${it.month}-${it.day}" } // Convert to "YYYY-MM-DD"
    }

    // Convert from String back to com.google.type.Date
    @TypeConverter
    fun toGoogleDate(dateString: String?): Date? {
        return dateString?.let {
            val parts = it.split("-")
            Date.newBuilder()
                .setYear(parts[0].toInt())
                .setMonth(parts[1].toInt())
                .setDay(parts[2].toInt())
                .build()
        }
    }

    // Convert from java.util.Date to Long
    @TypeConverter
    fun fromTimestamp(value: Long?): UtilDate? {
        return value?.let { UtilDate(it) }
    }

    // Convert from Long back to java.util.Date
    @TypeConverter
    fun dateToTimestamp(date: UtilDate?): Long? {
        return date?.time
    }
}
