package com.jencerio.listifyapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.ui.theme.GreenAccent
import com.jencerio.listifyapp.ui.theme.GreenSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingScreen(navController: NavHostController) {
    var budgetItems by remember { mutableStateOf(listOf<BudgetCategory>()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryAmount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BudgetCategory?>(null) }

    val totalIncome = budgetItems.filter { it.isIncome }.sumOf { it.amount }
    val totalExpense = budgetItems.filter { !it.isIncome }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

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
            FloatingActionButton(onClick = {
                categoryName = ""
                categoryAmount = ""
                isIncome = false
                editingItem = null
                isDialogOpen = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Budget Item")
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                SummaryCard(totalIncome, totalExpense, balance)
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(budgetItems.size) { index ->
                        BudgetItemRow(
                            budgetItem = budgetItems[index],
                            onEdit = {
                                editingItem = it
                                categoryName = it.category
                                categoryAmount = it.amount.toString()
                                isIncome = it.isIncome
                                isDialogOpen = true
                            },
                            onDelete = {
                                budgetItems = budgetItems - it
                            }
                        )
                    }
                }
            }
        }
    )

    if (isDialogOpen) {
        BudgetItemDialog(
            onDismiss = { isDialogOpen = false },
            onConfirm = {
                val newAmount = categoryAmount.toDoubleOrNull() ?: 0.0
                if (categoryName.isNotBlank() && newAmount > 0) {
                    if (editingItem != null) {
                        budgetItems = budgetItems.map {
                            if (it == editingItem) it.copy(category = categoryName, amount = newAmount, isIncome = isIncome) else it
                        }
                    } else {
                        budgetItems = budgetItems + BudgetCategory(categoryName, newAmount, isIncome)
                    }
                    isDialogOpen = false
                }
            },
            categoryName, { categoryName = it },
            categoryAmount, { categoryAmount = it },
            isIncome, { isIncome = it }
        )
    }
}

@Composable
fun BudgetItemRow(budgetItem: BudgetCategory, onEdit: (BudgetCategory) -> Unit, onDelete: (BudgetCategory) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFE4F8EC), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(budgetItem.category, fontWeight = FontWeight.Bold)
            Text("$${budgetItem.amount.formatAmount()}", fontWeight = FontWeight.Bold)
        }
        Row {
            IconButton(onClick = { onEdit(budgetItem) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { onDelete(budgetItem) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun BudgetItemDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    categoryName: String,
    setCategoryName: (String) -> Unit,
    categoryAmount: String,
    setCategoryAmount: (String) -> Unit,
    isIncome: Boolean,
    setIsIncome: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (categoryName.isEmpty()) "Add Budget Item" else "Edit Budget Item") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = setCategoryName,
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoryAmount,
                    onValueChange = setCategoryAmount,
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Income")
                    Switch(
                        checked = isIncome,
                        onCheckedChange = setIsIncome
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SummaryCard(totalIncome: Double, totalExpense: Double, balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenAccent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Income: $${totalIncome.formatAmount()}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("Expenses: $${totalExpense.formatAmount()}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("Balance: $${balance.formatAmount()}", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

data class BudgetCategory(val category: String, val amount: Double, val isIncome: Boolean)

fun Double.formatAmount(): String = "%.2f".format(this)