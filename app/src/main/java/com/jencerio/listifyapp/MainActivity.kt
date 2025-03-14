package com.jencerio.listifyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jencerio.listifyapp.ui.theme.ListifyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListifyApp()
        }
//        Log.d("MainActivity", "Checking if firestore helper is running...")
//        FirestoreHelper.testFirestoreConnection();
    }
}

@Preview(showBackground = true)
@Composable
fun ListifyApp() {
    val navController = rememberNavController()
    val shoppingListViewModel: ShoppingListViewModel = viewModel()

    // Check if user is already logged in
    val auth = FirebaseAuth.getInstance()
    val startDestination = if (auth.currentUser != null) {
        // User is already signed in, get username
        val displayName = auth.currentUser?.displayName
        val email = auth.currentUser?.email

        val username = when {
            !displayName.isNullOrEmpty() -> displayName
            !email.isNullOrEmpty() -> email.substringBefore("@") // Extract username from email
            else -> "User"
        }

        "dashboard/$username"  // Navigate directly to dashboard with username
    } else {
        "login"  // User is not signed in, show login screen
    }

    ListifyAppTheme {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        val showBottomBar = currentRoute !in listOf("login", "signup", "forgot_password", "opening")

        Scaffold(
            bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination, // Now uses dynamic start destination
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("login") { LoginScreen(navController) }
                composable("signup") { SignupScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("opening") { OpeningScreen(navController) }
                composable("dashboard/{displayName}") { backStackEntry ->
                    val displayName = backStackEntry.arguments?.getString("displayName") ?: "User"
                    DashboardScreen(navController, shoppingListViewModel, displayName)
                }

                composable("new_list") { AddNewListScreen(navController, shoppingListViewModel) }
                composable("shopping_list") {
                    ShoppingListScreen(
                        navController,
                        shoppingListViewModel
                    )
                }
                composable("budget_tracking") { BudgetTrackingScreen(navController) }
//                composable("set_reminder") { SetReminderScreen(navController) }
            }
        }
    }
}
