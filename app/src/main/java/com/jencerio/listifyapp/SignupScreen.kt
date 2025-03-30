package com.jencerio.listifyapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.database.AppDatabase
import com.jencerio.listifyapp.model.Users
import com.jencerio.listifyapp.ui.theme.greenDark
import com.jencerio.listifyapp.ui.theme.greenDarker
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun SignupScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Room Database and DAO
    val userDao = AppDatabase.getDatabase(context).userDao()

    // Firebase Authentication instance
    val auth = FirebaseAuth.getInstance()

    // Helper for error states
    var emailError by remember { mutableStateOf(false) }
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Regex pattern for email validation
    val emailPattern = Pattern.compile(
        "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )

    // Update error states based on validation
    fun validateFields() {
        emailError = !emailPattern.matcher(email).matches() // Email validation
        firstNameError = firstName.isEmpty()
        lastNameError = lastName.isEmpty()
        passwordError = password.length < 8 // Password length validation
        confirmPasswordError = confirmPassword.isEmpty() || confirmPassword != password
    }

    // FocusRequester and KeyboardController
    val emailFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Coroutine scope to launch tasks asynchronously
    val coroutineScope = rememberCoroutineScope()

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
            text = "Create Your Account",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // Signup title
        Text(
            text = "Sign Up",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50) // Green theme
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester),
            isError = emailError
        )

        Spacer(modifier = Modifier.height(8.dp))

        // First Name input
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = firstNameError
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name input
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = lastNameError
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign up Button
        Button(
            onClick = {
                validateFields()  // Check validation before submitting
                if (!emailError && !firstNameError && !lastNameError && !passwordError && !confirmPasswordError) {
                    if (password == confirmPassword) {
                        // Create a new User instance
                        val newUser = Users(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            password = password
                        )

                        // Launch coroutine to insert data into Room database
                        coroutineScope.launch {
                            userDao.insert(newUser)  // Save user to the Room database
                        }

                        // Create user in Firebase Authentication
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            Toast.makeText(context, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()
                                            navController.navigate("login") // Redirect to login for verification
                                        } else {
                                            Toast.makeText(context, "Failed to send verification email: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    val errorMessage = when (task.exception?.message) {
                                        "The email address is already in use by another account." ->
                                            "This email is already registered. Try logging in instead."
                                        "A network error (such as timeout, interrupted connection, or unreachable host) has occurred." ->
                                            "Network error. Please check your internet connection and try again."
                                        "The given password is invalid. [ PASSWORD_DOES_NOT_MEET_REQUIREMENTS:Missing password requirements: [Password must contain an upper case character, Password must contain a numeric character] ]" ->
                                            "Password must have at least one uppercase letter and one number."
                                        "We have blocked all requests from this device due to unusual activity. Try again later." ->
                                            "Too many signup attempts. Please wait and try again later."
                                        else -> "Signup failed: ${task.exception?.message ?: "Unknown error occurred."}"
                                    }

                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = greenDark)
        ) {
            Text("Sign Up", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Already have an account link
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Log in", color = greenDarker)
        }
    }

    // Request focus when the screen is launched
    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
        keyboardController?.show() // Show keyboard when the screen is launched
    }
}