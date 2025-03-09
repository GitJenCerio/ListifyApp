package com.jencerio.listifyapp

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jencerio.listifyapp.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(
    navController: NavHostController,
    shoppingListViewModel: ShoppingListViewModel,
    displayName: String,
) {
    val userDao = AppDatabase.getDatabase(LocalContext.current).userDao()
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

    LaunchedEffect(email) {
        val user = userDao.getUserByEmail(email)
        if (user != null) {
            firstName = user.firstName
            lastName = user.lastName
        }
    }

    val isOffline by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isOffline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFA000))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "You are in offline mode. Syncing is disabled. Changes will sync once online.",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Pass firstName and lastName instead of displayName
                ProfileSection(navController, displayName)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SHOPPING LIST",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileSection(
    navController: NavHostController,
    displayName: String
) {
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

        // Use displayName instead of firstName & lastName
        Text(
            text = "Hi $displayName!",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

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
fun BottomNavigationBar(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val displayName = extractUsernameFromEmail(currentUser?.email ?: "User") // Extract name from email

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xFF4CAF50) // Green color for the bottom bar
    ) {
        val navItems = listOf(
            Pair(R.drawable.baseline_home_24, "dashboard/$displayName"),
            Pair(R.drawable.baseline_edit_document_24, "shopping_list"),
            Pair(R.drawable.baseline_favorite_24, "favorites"),
            Pair(R.drawable.baseline_inventory_24, "pantry_inventory_management"),
            Pair(R.drawable.baseline_attach_money_24, "budget_tracking")
        )

        navItems.forEach { (icon, route) ->
            IconButton(
                onClick = { navController.navigate(route) },
                modifier = Modifier.weight(1f) // Even spacing
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = route,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
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
                    }
                    else if (index == 3) {
                        navController.navigate("budget_tracking")
                    }
                    else {
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

fun signOut(context: Context, navController: NavHostController) {
    // Sign out from Firebase
    FirebaseAuth.getInstance().signOut()

    // Clear user data from Room database
    CoroutineScope(Dispatchers.IO).launch {
        val userDao = AppDatabase.getDatabase(context).userDao()
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            userDao.deleteUserByEmail(email)  // Deleting the user data from Room
        }

        // Navigate to login screen after sign-out
        withContext(Dispatchers.Main) {
            navController.navigate("login") {
                popUpTo("dashboard") { inclusive = true }  // Clear back stack to prevent back navigation
            }
        }
    }
}



@Preview
@Composable
fun PreviewDashboardScreen() {
    val navController = rememberNavController()

    BottomNavigationBar(
        navController = navController,
    )
}