package com.example.yourapp.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.factory.BudgetViewModelFactory
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.model.ShoppingReminder
import com.jencerio.listifyapp.repository.BudgetRepository
import com.jencerio.listifyapp.viewmodel.BudgetViewModel
import com.jencerio.listifyapp.viewmodel.ShoppingReminderViewModel
import com.jencerio.listifyapp.viewmodel.ShoppingReminderViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SetReminderScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val budgetDao = AppDatabase.getDatabase(context).budgetDao()
    val repository = BudgetRepository(budgetDao)
    val viewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(repository))

    val budgetItems by viewModel.budgetItems.collectAsState()

    val userId = AuthHelper.getCurrentUserId()
    val totalIncome = budgetItems.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = budgetItems.filter { !it.isIncome }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val selectedDate = remember { mutableStateOf(dateFormat.format(Calendar.getInstance().time)) }
    val messageText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Set a Reminder Date", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    selectedDate.value = "$year-${month + 1}-$dayOfMonth"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }) {
            Text("Pick a date")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selected Date: ${selectedDate.value}")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = messageText.value,
            onValueChange = { messageText.value = it },
            label = { Text("Reminder Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (selectedDate.value.isNotEmpty()) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

                try {
                    // Parse the selected date
                    val date = dateFormat.parse(selectedDate.value) ?: Date()

                    val reminder = ShoppingReminder(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        shoppingListId = null,
                        reminderDate = date,
                        message = messageText.value
                    )

//                    scope.launch {
//                        reminderViewModel.addShoppingReminderItem(reminder)
//                        messageText.value = ""
//                    }
                } catch (e: Exception) {
                    Log.e("SetReminderScreen", "Error saving reminder: ${e.message}")
                }
            }
        }) {
            Text("Save Reminder")
        }
    }
}