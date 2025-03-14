package com.jencerio.listifyapp.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.jencerio.listifyapp.dao.ShoppingReminderDao
import com.jencerio.listifyapp.model.ShoppingReminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ShoppingReminderRepository(private val shoppingReminderDao: ShoppingReminderDao?) { // 🔹 Make it optional

}
