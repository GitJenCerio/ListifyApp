package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import utility_functions.User
import utility_functions.registerUser
import android.content.Context
import android.util.Log
import android.widget.EditText


//class SignUpScreenView : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.register)
//    }
//}

@Composable
fun SignupScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.register, null)
        },
        modifier = Modifier.fillMaxSize()
    ) { view ->
        val registerButton: Button = view.findViewById(R.id.registerBtn)
        registerButton.setOnClickListener {

            email = view.findViewById<EditText>(R.id.email).text.toString()
            firstName = view.findViewById<EditText>(R.id.firstname).text.toString()
            lastName = view.findViewById<EditText>(R.id.lastname).text.toString()
            password = view.findViewById<EditText>(R.id.password).text.toString()
            confirmPassword = view.findViewById<EditText>(R.id.repeat_password).text.toString()

            Log.d("SignupScreen", "Email: $email, FirstName: $firstName, LastName: $lastName, Password: $password, ConfirmPassword: $confirmPassword")

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password == confirmPassword) {
                val newUser = User(email, password, firstName, lastName)
                registerUser(context, newUser)
                navController.navigate("login")
            } else {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
