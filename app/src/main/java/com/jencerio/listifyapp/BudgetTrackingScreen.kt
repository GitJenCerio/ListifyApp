package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BudgetTrackingScreen() {
    var budget by remember { mutableStateOf(100.0) }  // Default budget value
    var totalSpent by remember { mutableStateOf(0.0) }  // Sum of items added

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Budget Tracking", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Budget: $${String.format("%.2f", budget)}")
        Text("Total Spent: $${String.format("%.2f", totalSpent)}")
        if (totalSpent > budget) {
            Text("Budget exceeded!", color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}
