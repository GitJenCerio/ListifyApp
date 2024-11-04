package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController

@Composable
fun ShoppingListScreen(
    navController: NavHostController,
    shoppingListViewModel: ShoppingListViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedShoppingList by remember { mutableStateOf<ShoppingList?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Shopping Lists",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (shoppingListViewModel.shoppingLists.isEmpty()) {
            Text(
                text = "No shopping lists created yet.",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(shoppingListViewModel.shoppingLists) { shoppingList ->
                    ShoppingListItem(
                        shoppingList = shoppingList,
                        onEdit = {
                            selectedShoppingList = shoppingList
                            showDialog = true
                        },
                        onDelete = {
                            shoppingListViewModel.removeShoppingList(shoppingList)
                        }
                    )
                }
            }
        }
    }

    // Dialog for editing shopping list
    if (showDialog && selectedShoppingList != null) {
        EditShoppingListDialog(
            shoppingList = selectedShoppingList!!,
            onDismiss = { showDialog = false },
            onSave = { updatedName, updatedItems ->
                shoppingListViewModel.updateShoppingList(selectedShoppingList!!, updatedName, updatedItems)
                showDialog = false
            }
        )
    }
}

@Composable
fun EditShoppingListDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onSave: (String, List<ShoppingItem>) -> Unit
) {
    var listName by remember { mutableStateOf(shoppingList.name) }
    var listItems by remember { mutableStateOf(shoppingList.items.map { it.copy() }.toMutableList()) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Shopping List",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Editable text field for list name
                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // List of items with editable quantity fields
                listItems.forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Item name
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f),
                            style = TextStyle(fontSize = 16.sp, color = Color.Black)
                        )

                        // Editable quantity field
                        OutlinedTextField(
                            value = item.quantity.takeIf { it > 0 }?.toString() ?: "",
                            onValueChange = { newQuantity ->
                                listItems = listItems.toMutableList().apply {
                                    if (newQuantity.isBlank()) {
                                        set(index, item.copy(quantity = 0))
                                    } else {
                                        val quantity = newQuantity.toIntOrNull() ?: item.quantity
                                        set(index, item.copy(quantity = quantity))
                                    }
                                }
                            },
                            label = { Text("Qty") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section for adding a new item
                Text("Add New Item", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4CAF50))

                // New Item Name field
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                // New Item Quantity field
                OutlinedTextField(
                    value = newItemQuantity,
                    onValueChange = { newItemQuantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = Color.Black)
                )

                // Add Button
                Button(
                    onClick = {
                        val quantity = newItemQuantity.toIntOrNull() ?: 0
                        if (newItemName.isNotBlank()) {
                            listItems.add(ShoppingItem(newItemName, quantity))
                            newItemName = ""
                            newItemQuantity = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Add Item")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val updatedItems = listItems.filter { it.quantity > 0 }
                            onSave(listName, updatedItems)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
