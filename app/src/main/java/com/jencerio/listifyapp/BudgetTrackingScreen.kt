package com.jencerio.listifyapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.ui.theme.GreenAccent
import com.jencerio.listifyapp.ui.theme.GreenSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingScreen(navController: NavHostController) {
    // State for tracking the list of budget items
    var budgetItems by remember { mutableStateOf(
        listOf(
            BudgetCategory("Rent", 1200.00),
            BudgetCategory("Groceries", 300.00),
            BudgetCategory("Entertainment", 150.00),
            BudgetCategory("Transportation", 100.00)
        )
    )}

    // State to handle the dialog visibility
    var isDialogOpen by remember { mutableStateOf(false) }

    // States for input fields in the dialog
    var categoryName by remember { mutableStateOf("") }
    var categoryAmount by remember { mutableStateOf("") }

    // Calculate the total budget
    val totalBudget = remember(budgetItems) {
        budgetItems.sumOf { it.amount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Tracking", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(GreenSecondary),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isDialogOpen = true }, // Open the form dialog when clicked
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add Budget Item")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Monthly Budget Summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(GreenAccent, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Monthly Budget",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "$${totalBudget.formatAmount()}",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                    )
                }

                // Budget Breakdown List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(budgetItems.size) { index ->
                        BudgetItemRow(budgetItem = budgetItems[index])
                    }
                }
            }
        }
    )

    // Budget Item Add Dialog
    if (isDialogOpen) {
        BudgetItemDialog(
            onDismiss = { isDialogOpen = false },
            onConfirm = {
                // Create new budget item and add to the list
                val newAmount = categoryAmount.toDoubleOrNull() ?: 0.0
                if (categoryName.isNotBlank() && newAmount > 0) {
                    budgetItems = budgetItems + BudgetCategory(categoryName, newAmount)
                    isDialogOpen = false // Close the dialog after adding
                }
            },
            categoryName = categoryName,
            setCategoryName = { categoryName = it },
            categoryAmount = categoryAmount,
            setCategoryAmount = { categoryAmount = it }
        )
    }
}

@Composable
fun BudgetItemRow(budgetItem: BudgetCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFE4F8EC), shape = RoundedCornerShape(16.dp)) // Oval-like shape
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = budgetItem.category,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$${budgetItem.amount.formatAmount()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

// Helper function to format the amount
fun Double.formatAmount(): String {
    return "%.2f".format(this)
}

// Dialog for adding a new budget item
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetItemDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    categoryName: String,
    setCategoryName: (String) -> Unit,
    categoryAmount: String,
    setCategoryAmount: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Budget Item") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = setCategoryName,
                    label = { Text("Budget Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoryAmount,
                    onValueChange = setCategoryAmount,
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class BudgetCategory(val category: String, val amount: Double)