package com.jencerio.listifyapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class Users (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String

)
