package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jencerio.listifyapp.viewmodel.BudgetViewModel
import com.jencerio.listifyapp.factory.BudgetViewModelFactory
import com.jencerio.listifyapp.model.BudgetCategory
import com.jencerio.listifyapp.repository.BudgetRepository
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.jencerio.listifyapp.database.BudgetDatabase
import com.jencerio.listifyapp.ui.theme.GreenAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val budgetDao = BudgetDatabase.getDatabase(context).budgetDao()
    val repository = BudgetRepository(budgetDao)
    val viewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(repository))

    val budgetItems by viewModel.budgetItems.collectAsState()

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
                title = { Text("Budget Tracking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
                                viewModel.deleteBudgetItem(it)
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
                        viewModel.updateBudgetItem(editingItem!!.copy(category = categoryName, amount = newAmount, isIncome = isIncome))
                    } else {
                        viewModel.addBudgetItem(BudgetCategory(category = categoryName, amount = newAmount, isIncome = isIncome))
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
fun SummaryCard(totalIncome: Double, totalExpense: Double, balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenAccent)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Income: $totalIncome", fontWeight = FontWeight.Bold, color = Color.White)
            Text("Total Expense: $totalExpense", fontWeight = FontWeight.Bold, color = Color.White)
            Text("Balance: $balance", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun BudgetItemRow(budgetItem: BudgetCategory, onEdit: (BudgetCategory) -> Unit, onDelete: (BudgetCategory) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(budgetItem.category, fontWeight = FontWeight.Bold)
                Text("${budgetItem.amount}", color = if (budgetItem.isIncome) Color.Green else Color.Red)
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
}

@Composable
fun BudgetItemDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    categoryName: String,
    onCategoryNameChange: (String) -> Unit,
    categoryAmount: String,
    onCategoryAmountChange: (String) -> Unit,
    isIncome: Boolean,
    onIsIncomeChange: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Budget Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = onCategoryNameChange,
                    label = { Text("Category Name") }
                )
                OutlinedTextField(
                    value = categoryAmount,
                    onValueChange = onCategoryAmountChange,
                    label = { Text("Amount") }
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Income")
                    Switch(checked = isIncome, onCheckedChange = onIsIncomeChange)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

