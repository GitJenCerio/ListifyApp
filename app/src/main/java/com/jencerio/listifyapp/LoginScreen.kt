package com.jencerio.listifyapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jencerio.listifyapp.R
import com.jencerio.listifyapp.common.composable.EmailTextField
import com.jencerio.listifyapp.common.composable.ForgotPasswordText
import com.jencerio.listifyapp.common.composable.FullWidthButton
import com.jencerio.listifyapp.common.composable.PasswordTextField
import com.jencerio.listifyapp.ui.theme.greenDark
import com.jencerio.listifyapp.ui.theme.greenDarker

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Set up Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("1:715719013362:android:4bd51156cfa9a48509716b")  // Replace with your actual Web Client ID from Firebase Console
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // ActivityResultLauncher to handle Google Sign-In result
    val signInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    // Firebase authentication with Google credentials
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("opening") // Navigate to the home screen after successful sign-in
                            } else {
                                Log.w("Google Sign-In", "signInWithCredential:failure", task.exception)
                                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                Log.w("Google Sign-In", "signInWithGoogle:failure", e)
                Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Handle sign-in failure
            Log.w("Google Sign-In", "Google sign-in failed")
        }
    }
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
                    // Navigate to dashboard after successful sign-in
                    navController.navigate("dashboard")
                } else {
                    // Handle failure
                    Log.w("Email Sign-In", "signInWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
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
                // Validate email and password inputs before attempting login
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

        FullWidthButton(
            label = "Continue Without Account",
            onClick = {
                navController.navigate("opening")
            },
            color = greenDarker
        )

        ForgotPasswordText {
            // Implement forgot password logic
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        FullWidthButton(
            label = "Create Account",
            onClick = {
                navController.navigate("signup")
            },
            color = greenDarker,
            modifier = Modifier
        )
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        navController = rememberNavController()
    )
}
