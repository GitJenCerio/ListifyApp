package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
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
    var newItemUnit by remember { mutableStateOf(units.first()) }
    var newItemUnitExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 600.dp, max = 700.dp), // Adjust dialog height
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
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Editable items
                LazyColumn {
                    itemsIndexed(listItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = item.name,
                                modifier = Modifier.weight(1f),
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )

                            OutlinedTextField(
                                value = item.quantity.takeIf { it > 0 }?.toString() ?: "",
                                onValueChange = { newQuantity ->
                                    val parsedQuantity = newQuantity.toIntOrNull() ?: 0
                                    listItems[index] = item.copy(quantity = parsedQuantity)
                                },
                                label = { Text("Qty") },
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(end = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Box(modifier = Modifier.width(120.dp)) {
                                OutlinedTextField(
                                    value = item.unit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unit") },
                                    trailingIcon = {
                                        IconButton(onClick = { /* Handle unit selection */ }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                                                contentDescription = "Select unit"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add new item section
                Text(
                    text = "Add New Item",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newItemQuantity,
                    onValueChange = { newItemQuantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    OutlinedTextField(
                        value = newItemUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            IconButton(onClick = { newItemUnitExpanded = !newItemUnitExpanded }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                                    contentDescription = "Select unit"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = newItemUnitExpanded,
                        onDismissRequest = { newItemUnitExpanded = false }
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    newItemUnit = unit
                                    newItemUnitExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (newItemName.isNotBlank() && newItemQuantity.toIntOrNull() != null) {
                            listItems.add(
                                ShoppingItem(
                                    name = newItemName,
                                    quantity = newItemQuantity.toInt(),
                                    unit = newItemUnit
                                )
                            )
                            newItemName = ""
                            newItemQuantity = ""
                            newItemUnit = units.first()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Item")
                }

                // Save and Cancel buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { onSave(listName, listItems) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
