package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.Users
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {

    // Insert a new user
    @Insert
    suspend fun insert(user: Users)

    // Get a user by email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Users?

    // Update user
    @Update
    suspend fun update(user: Users)

    // Delete user
    @Delete
    suspend fun delete(user: Users)

    // Get all users (if needed)
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<Users>>

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUserByEmail(email: String)

}