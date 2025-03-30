package com.jencerio.listifyapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.repository.BudgetRepository
import com.jencerio.listifyapp.repository.ShoppingListRepository
import com.jencerio.listifyapp.ui.theme.ListifyAppTheme
import com.jencerio.listifyapp.viewmodel.BudgetViewModel
import com.jencerio.listifyapp.viewmodel.ShoppingListViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListifyApp()
        }
        scheduleSyncWorker(this)
//        Log.d("MainActivity", "Checking if firestore helper is running...")
//        FirestoreHelper.testFirestoreConnection();
    }

    @SuppressLint("ServiceCast")
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun fetchBudgets() {
        val budgetViewModel = BudgetViewModel(BudgetRepository(AppDatabase.getDatabase(this).budgetDao()))
        lifecycleScope.launch {
            budgetViewModel.syncBudgetPendingItems()
        }
    }

    private fun fetchShoppingLists() {
        val shoppingListViewModel = ShoppingListViewModel(ShoppingListRepository(AppDatabase.getDatabase(this).shoppingListDao()))
        lifecycleScope.launch {
            shoppingListViewModel.syncShoppingListPendingItems()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListifyApp() {
    val navController = rememberNavController()

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
                    DashboardScreen(navController, displayName)
                }

                composable("shopping_list") {
                    ShoppingListScreen(
                        navController,
                    )
                }
                composable("budget_tracking") { BudgetTrackingScreen(navController) }
//                composable("set_reminder") { SetReminderScreen(navController) }
            }
        }
    }
}


fun scheduleSyncWorker(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Only sync when internet is available
        .build()

    val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "SyncWorker",
        ExistingPeriodicWorkPolicy.KEEP,
        syncRequest
    )
}