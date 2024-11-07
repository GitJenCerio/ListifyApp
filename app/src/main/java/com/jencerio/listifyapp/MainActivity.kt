package com.jencerio.listifyapp

import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jencerio.listifyapp.ui.theme.ListifyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListifyApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListifyApp() {
    val navController = rememberNavController()
    val shoppingListViewModel: ShoppingListViewModel = viewModel()

    ListifyAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login", // Start with the login screen
                modifier = Modifier.padding(innerPadding)
            ) {
                // Define navigation routes
                composable("login") { LoginScreen(navController) }
                composable("opening") { OpeningScreen(navController) }
                composable("dashboard") { DashboardScreen(navController, shoppingListViewModel) }
                composable("new_list") { AddNewListScreen(navController, shoppingListViewModel) }
                composable("shopping_list") { ShoppingListScreen(navController, shoppingListViewModel) }
                composable("signup") { SignupScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("offline") { OfflineScreen() }
                composable("budget_tracking") { BudgetTrackingScreen(
                    navController
                ) }
            }
        }
    }
}



@Composable
fun OfflineScreen() {
    TODO("Not yet implemented")
}

