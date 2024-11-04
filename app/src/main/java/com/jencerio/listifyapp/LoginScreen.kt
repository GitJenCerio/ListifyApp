package com.jencerio.listifyapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.R

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )

        // Tagline
        Text(
            text = "Grocery Planning, Made Simple.",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Login title
        Text(
            text = "Login",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email TextField with reduced width and no underline
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text("Enter email", color = Color.White)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width to 90% of parent
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFF66BB6A), // Lighter green background
                textColor = Color.White,
                focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                unfocusedIndicatorColor = Color.Transparent // Remove underline when unfocused
            ),
            shape = RoundedCornerShape(30.dp)
        )

        // Password TextField with reduced width and no underline
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text("Enter password", color = Color.White)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width to 90% of parent
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFF66BB6A), // Lighter green background
                textColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp)
        )

        Button(
            onClick = {
                navController.navigate("opening") // Navigate to the opening page after login
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Login",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }


        // Forgot Password TextButton
        TextButton(
            onClick = { navController.navigate("forgot_password") },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Forgot Password",
                style = TextStyle(
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Create Account Button with reduced width
        Button(
            onClick = { navController.navigate("signup") },
            modifier = Modifier
                .fillMaxWidth(0.9f) // Adjust width to 90% of parent
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Create Account",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
