//package com.example.yourapp.ui.screens
//
//import android.app.DatePickerDialog
//import android.content.Context
//import android.util.Log
//import androidx.activity.viewModels
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.compose.viewModel
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.jencerio.listifyapp.model.ShoppingReminder
//import com.jencerio.listifyapp.repository.ShoppingReminderRepository
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import java.util.UUID
//
//class ShoppingReminderViewModel(
//    private val repository: ShoppingReminderRepository
////) : ViewModel() {
////    val coroutineScope = rememberCoroutineScope()
////
////    suspend fun addReminder(reminder: ShoppingReminder) {
////        try {
////            repository.addShoppingReminder(reminder)
////            // Sync with Firestore
////            val db = FirebaseFirestore.getInstance()
////            val reminderData = hashMapOf(
////                "id" to reminder.id,
////                "userId" to reminder.userId,
////                "date" to reminder.reminderDate,
////            )
////            db.collection("shopping_reminders").document(reminder.id).set(reminder).await()
////        } catch (e: Exception) {
////            Log.e("ShoppingReminderRepo", "Error adding reminder", e)
////        }
////    }
////}
//
//@Composable
//fun SetReminderScreen(reminderViewModel: ShoppingReminderViewModel = viewModel()) {
//    val context = LocalContext.current
//    val calendar = Calendar.getInstance()
//    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//    val selectedDate = remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)) }
//    val shoppingReminderRepository = remember { ShoppingReminderRepository(shoppingReminderDao = null) }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text("Set a Reminder Date")
//        Button(onClick = {
//            val datePickerDialog = DatePickerDialog(
//                context,
//                { _, year, month, dayOfMonth ->
//                    selectedDate.value = "$year-${month + 1}-$day"
//                },
//                Calendar.getInstance().get(Calendar.YEAR),
//                Calendar.getInstance().get(Calendar.MONTH),
//                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//            )
//            datePicker.show()
//        }) {
//            Text("Pick a date")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(text = "Selected Date: \${selectedDate.value}")
//    }
//
//    val context = LocalContext.current
//    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    val firestore = FirebaseFirestore.getInstance()
//    val shoppingReminderRepository = ShoppingReminderRepository(ShoppingReminderDao())
//
//    Button(onClick = {
//        if (selectedDate.value.isNotEmpty()) {
//            val reminder = ShoppingReminder(
//                id = UUID.randomUUID().toString(),
//                userId = userId ?: "unknown",
//                reminderDate = Timestamp.now()
//            )
//            shoppingReminderRepository.addReminder(reminder)
//        }
//    }) {
//        Text("Save Reminder")
//    }
//}
