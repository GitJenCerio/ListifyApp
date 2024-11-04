package com.jencerio.listifyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun DashboardScreen(navController: NavHostController, shoppingListViewModel: ShoppingListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileSection(navController)
        Spacer(modifier = Modifier.height(16.dp))
        ActionButtons(navController)
        Spacer(modifier = Modifier.height(16.dp))

        Text("SHOPPING LIST", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            if (shoppingListViewModel.shoppingLists.isEmpty()) {
                item {
                    Text(
                        text = "You have no shopping lists yet.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                }
            } else {
                items(shoppingListViewModel.shoppingLists) { list ->
                    Text(list.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black, modifier = Modifier.padding(vertical = 4.dp))
                    list.items.forEach { item ->
                        Text("- ${item.name} (x${item.quantity})", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(start = 16.dp, bottom = 4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("new_list") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Add New List")
        }
    }
}

@Composable
fun ProfileSection(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4CAF50))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hi Jane Doe!",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Set Shopping Reminders button
        Button(
            onClick = { navController.navigate("set_reminder") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text = "Set Shopping Reminders", color = Color(0xFF4CAF50))
        }
    }
}

@Composable
fun ActionButtons(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val icons = listOf(
            R.drawable.ic_list, // First icon - Shopping List Screen
            R.drawable.ic_heart,
            R.drawable.ic_share,
            R.drawable.ic_budget
        )

        icons.forEachIndexed { index, icon ->
            IconButton(
                onClick = {
                    if (index == 0) {
                        navController.navigate("shopping_list")
                    } else {
                        // Handle other actions
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF66BB6A), shape = MaterialTheme.shapes.medium)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@Composable
fun ShoppingListSection(items: List<ShoppingItem>) {
    Text(
        text = "Items in List:",
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        modifier = Modifier.padding(8.dp)
    )
    items.forEach { item ->
        Text(
            text = "- ${item.name} (x${item.quantity})",
            style = TextStyle(fontSize = 14.sp, color = Color.Gray),
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
    }
}


@Composable
fun ShoppingListItem(
    shoppingList: ShoppingList,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(
            text = shoppingList.name,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        shoppingList.items.forEach { item ->
            Text(
                text = "- ${item.name} (x${item.quantity})",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onEdit) {
                Text(text = "Edit", color = Color(0xFF4CAF50))
            }
            TextButton(onClick = onDelete) {
                Text(text = "Delete", color = Color.Red)
            }
        }
    }
}
