package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController



@Composable
fun AddNewListScreen(navController: NavHostController, shoppingListViewModel: ShoppingListViewModel) {
    var listName by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<ShoppingItem>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create a New List", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))

        OutlinedTextField(
            value = listName,
            onValueChange = { listName = it },
            label = { Text("List Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                val quantity = newItemQuantity.toIntOrNull() ?: 1
                if (newItemName.isNotBlank()) {
                    items.add(ShoppingItem(newItemName, quantity))
                    newItemName = ""
                    newItemQuantity = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Add Item")
        }

        if (items.isNotEmpty()) {
            Text("Items in List:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            items.forEach { item ->
                Text("- ${item.name} (x${item.quantity})", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
            }
        } else {
            Text("No items added yet.", color = Color.Gray)
        }

        Button(
            onClick = {
                if (listName.isNotBlank()) {
                    shoppingListViewModel.addShoppingList(listName, items.toList())
                    navController.popBackStack() // Go back to dashboard
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Save List")
        }
    }
}
