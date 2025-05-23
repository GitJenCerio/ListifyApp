package com.jencerio.listifyapp

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jencerio.listifyapp.common.composable.EmailTextField
import com.jencerio.listifyapp.common.composable.FullWidthButton
import com.jencerio.listifyapp.common.composable.PasswordTextField
import com.jencerio.listifyapp.ui.theme.greenDark
import com.jencerio.listifyapp.ui.theme.greenDarker

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var resetEmail by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()


    // Set up Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("715719013362-or3qte6vttfdkk5omfcgr50bjni6dq7j.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            // Set navController to "opening" while processing the sign-in
            navController.navigate("opening")

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val username = extractUsernameFromEmail(account.email ?: "User")
                                navController.navigate("dashboard/$username") {
                                    popUpTo("opening") { inclusive = true } // Remove "opening" from the stack
                                }
                            } else {
                                Log.w("Google Sign-In", "signInWithCredential:failure", task.exception)
                                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // Go back if failed
                            }
                        }
                }
            } catch (e: ApiException) {
                Log.e("Google Sign-In", "Error Code: ${e.statusCode} - ${e.localizedMessage}")
                Toast.makeText(context, "Google Sign-In failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
                navController.popBackStack() // Go back if exception occurs
            }
        } else {
            Log.w("Google Sign-In", "Google sign-in failed")
        }
    }

    // Function to handle Email/Password sign-in
    @RequiresApi(Build.VERSION_CODES.O)
    fun handleEmailPasswordSignIn(
        email: String,
        password: String,
        auth: FirebaseAuth,
        context: Context,
        navController: NavHostController
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        val username = extractUsernameFromEmail(user.email ?: "User") // Extract username
                        Toast.makeText(context, "Welcome back, $username!", Toast.LENGTH_SHORT).show()

                        // Pass the username as an argument
                        navController.navigate("dashboard/$username")
                    }
                } else {
                    Log.w("Email Sign-In", "signInWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Function to handle the forgot password logic
    fun handleForgotPassword(email: String, context: Context) {
        if (email.isEmpty()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                showForgotPasswordDialog = false  // Close dialog after request is complete
            }
    }

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
                color = Color(0xFF4CAF50) // Green theme
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email input
        EmailTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            isError = false,
            errorText = ""
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isError = false,
            errorText = ""
        )

        // Login Button (Email/Password)
        FullWidthButton(
            label = "Login",
            onClick = {
                handleEmailPasswordSignIn(email, password, auth, context, navController)
            },
            color = greenDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In Button
        FullWidthButton(
            label = "Sign in with Google",
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                signInLauncher.launch(signInIntent)
            },
            color = Color(0xFFDB4437) // Google Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password Button
        TextButton(onClick = { showForgotPasswordDialog = true }) {
            Text("Forgot Password?", color = greenDarker)
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Reset Password") },
                text = {
                    Column {
                        Text("Enter your email address to receive a password reset link.")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { handleForgotPassword(resetEmail, context) },
                        colors = ButtonDefaults.buttonColors(containerColor = greenDark)
                    ) {
                        Text("Send Reset Link", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Create Account", color = greenDarker)
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}


// Function to extract username (remove @domain)
fun extractUsernameFromEmail(email: String): String {
    return email.substringBefore("@") // Get everything before '@'
}