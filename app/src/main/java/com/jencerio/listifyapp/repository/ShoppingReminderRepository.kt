package com.jencerio.listifyapp.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jencerio.listifyapp.dao.ShoppingReminderDao
import com.jencerio.listifyapp.model.ShoppingReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class ShoppingReminderRepository(private val shoppingReminderDao: ShoppingReminderDao) {
    private val firestore = FirebaseFirestore.getInstance()
    private val remindersCollection = firestore.collection("shopping_reminders")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    // Get all reminders from local database
    fun getShoppingReminders(): Flow<List<ShoppingReminder>> {
        return shoppingReminderDao.getAll()
    }

    // Add reminder to local database first, then sync with Firestore
    suspend fun addShoppingReminder(shoppingReminder: ShoppingReminder) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Store locally first
                shoppingReminderDao.insert(shoppingReminder)

                // 2. Then sync with remote server
                val reminderMap = mapOf(
                    "id" to shoppingReminder.id,
                    "userId" to shoppingReminder.userId,
                    "shoppingListId" to (shoppingReminder.shoppingListId ?: ""),
                    "reminderDate" to Timestamp(shoppingReminder.reminderDate),
                    "message" to shoppingReminder.message,
                    "lastSynced" to Timestamp.now()
                )

                remindersCollection.document(shoppingReminder.id)
                    .set(reminderMap)
                    .await()
            } catch (e: Exception) {
                // If remote sync fails, local data is still preserved
                // Could add sync status flag to model for retry logic
            }
        }
    }

    // Update reminder in local database first, then sync with Firestore
    suspend fun updateShoppingReminder(shoppingReminder: ShoppingReminder) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Update locally first
                shoppingReminderDao.update(shoppingReminder)

                // 2. Then sync with remote server
                val reminderMap = mapOf(
                    "id" to shoppingReminder.id,
                    "userId" to shoppingReminder.userId,
                    "shoppingListId" to (shoppingReminder.shoppingListId ?: ""),
                    "reminderDate" to Timestamp(shoppingReminder.reminderDate),
                    "message" to shoppingReminder.message,
                    "lastSynced" to Timestamp.now()
                )

                remindersCollection.document(shoppingReminder.id)
                    .update(reminderMap)
                    .await()
            } catch (e: Exception) {
                // If remote sync fails, local data is still preserved
            }
        }
    }

    // Delete reminder from local database first, then sync with Firestore
    suspend fun deleteShoppingReminder(shoppingReminder: ShoppingReminder) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Delete locally first
                shoppingReminderDao.delete(shoppingReminder)

                // 2. Then sync with remote server
                remindersCollection.document(shoppingReminder.id)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // If remote sync fails, consider adding a "deleted" flag to handle this case
            }
        }
    }

    // Sync local data with remote server (called periodically or on network reconnection)
    suspend fun syncWithRemoteServer() {
        withContext(Dispatchers.IO) {
            try {
                // Get all local reminders
                val localReminders = shoppingReminderDao.getAllAsList()

                // Get all remote reminders for this user
                val remoteReminders = remindersCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        try {
                            val id = doc.getString("id") ?: return@mapNotNull null
                            val remoteUserId = doc.getString("userId") ?: return@mapNotNull null
                            val shoppingListId = doc.getString("shoppingListId")
                            val reminderDate = (doc.getTimestamp("reminderDate")?.toDate() ?: Date())
                            val message = doc.getString("message") ?: ""

                            ShoppingReminder(id, remoteUserId, shoppingListId, reminderDate, message)
                        } catch (e: Exception) {
                            null
                        }
                    }

                // Update local database with any new or updated remote reminders
                for (remoteReminder in remoteReminders) {
                    val localReminder = localReminders.find { it.id == remoteReminder.id }
                    if (localReminder == null) {
                        // Remote reminder not in local database, add it
                        shoppingReminderDao.insert(remoteReminder)
                    } else {
                        // Compare timestamps and update if remote is newer
                        // This would require storing lastModified timestamps
                    }
                }

                // Push any local reminders not in remote
                for (localReminder in localReminders) {
                    if (remoteReminders.none { it.id == localReminder.id }) {
                        // Local reminder not in remote, push it
                        val reminderMap = mapOf(
                            "id" to localReminder.id,
                            "userId" to localReminder.userId,
                            "shoppingListId" to (localReminder.shoppingListId ?: ""),
                            "reminderDate" to Timestamp(localReminder.reminderDate),
                            "message" to localReminder.message,
                            "lastSynced" to Timestamp.now()
                        )

                        remindersCollection.document(localReminder.id)
                            .set(reminderMap)
                            .await()
                    }
                }
            } catch (e: Exception) {
                // Handle sync errors
            }
        }
    }
}