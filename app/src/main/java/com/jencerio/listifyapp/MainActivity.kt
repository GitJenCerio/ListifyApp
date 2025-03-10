package com.jencerio.listifyapp

import android.os.Bundle
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.repository.FirestoreRepository
import com.jencerio.listifyapp.repository.BudgetRepository
import com.jencerio.listifyapp.ui.theme.ListifyAppTheme
import com.jencerio.listifyapp.viewmodel.BudgetViewModel
import com.jencerio.listifyapp.factory.BudgetViewModelFactory
import com.jencerio.listifyapp.factory.ShoppingListViewModelFactory
import com.jencerio.listifyapp.repository.ShoppingListRepository
import com.jencerio.listifyapp.viewmodel.ShoppingListViewModel


class MainActivity : ComponentActivity() {
    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get database instance
        val database = AppDatabase.getDatabase(this)
        val budgetDao = database.budgetDao()
        val shoppingListDao = database.shoppingListDao()

        // Initialize FirestoreRepository with required DAO dependencies
        firestoreRepository = FirestoreRepository(budgetDao, shoppingListDao)

        setContent {
            ListifyApp(firestoreRepository)
        }
    }
}



@Composable
fun ListifyApp(firestoreRepository: FirestoreRepository) {
    val navController = rememberNavController()

    // Initialize repositories
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val budgetDao = database.budgetDao()
    val shoppingListDao = database.shoppingListDao()

    // Create repositories
    val budgetRepository = BudgetRepository(budgetDao)
    val shoppingListRepository = ShoppingListRepository(shoppingListDao) // Ensure this exists

    // Create ViewModels
    val budgetViewModel: BudgetViewModel = viewModel(factory = BudgetViewModelFactory(budgetRepository))
    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(shoppingListRepository))

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
                startDestination = "login",
                modifier = Modifier.padding(innerPadding),
            ) {
                composable("login") { LoginScreen(navController) }
                composable("signup") { SignupScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("opening") { OpeningScreen(navController) }
                composable("dashboard/{displayName}") { backStackEntry ->
                    val displayName = backStackEntry.arguments?.getString("displayName") ?: "User"
                    DashboardScreen(navController,shoppingListViewModel, displayName)
                }
                composable("new_list") { AddNewListScreen(navController, shoppingListViewModel) }
                composable("shopping_list") { ShoppingListScreen(navController, shoppingListViewModel) }
                composable("budget_tracking") { BudgetTrackingScreen(navController) }
                composable("pantry_inventory_management") { PantryInventoryManagementScreen(navController) }
            }
        }
    }
}


