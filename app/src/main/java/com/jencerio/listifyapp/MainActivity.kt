package com.jencerio.listifyapp

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        val showBottomBar = currentRoute !in listOf("login", "signup", "forgot_password", "opening")

        Scaffold(
            bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login", // Start with login screen
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("login") { LoginScreen(navController) }
                composable("signup") { SignupScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("opening") { OpeningScreen(navController) }
                composable("dashboard") { DashboardScreen(navController, shoppingListViewModel) }
                composable("new_list") { AddNewListScreen(navController, shoppingListViewModel) }
                composable("shopping_list") { ShoppingListScreen(navController, shoppingListViewModel)
                }
                composable("budget_tracking") { BudgetTrackingScreen(navController) }
            }
        }
    }
}
