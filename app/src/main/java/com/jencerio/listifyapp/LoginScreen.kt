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
import com.jencerio.listifyapp.R
import com.jencerio.listifyapp.common.composable.EmailTextField
import com.jencerio.listifyapp.common.composable.ForgotPasswordText
import com.jencerio.listifyapp.common.composable.FullWidthButton
import com.jencerio.listifyapp.common.composable.PasswordTextField
import com.jencerio.listifyapp.ui.theme.greenDark
import com.jencerio.listifyapp.ui.theme.greenDarker
import com.jencerio.listifyapp.ui.theme.secondaryDark
import com.jencerio.listifyapp.ui.theme.secondaryMedium
import utility_functions.loadUsers
import java.nio.file.WatchEvent

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

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

// Email TextField
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            placeholder = { Text("Enter email", color = Color.White) },
//            singleLine = true,
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .padding(vertical = 8.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color(0xFF66BB6A),
//                unfocusedContainerColor = Color(0xFF66BB6A),
//                focusedTextColor = Color.White,
//                unfocusedTextColor = Color.White,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedLabelColor = Color.White,
//                unfocusedLabelColor = Color.White
//            ),
//            shape = RoundedCornerShape(30.dp)
//        )

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

//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            placeholder = { Text("Enter password", color = Color.White) },
//            singleLine = true,
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .padding(vertical = 8.dp),
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = Color(0xFF66BB6A),
//                unfocusedContainerColor = Color(0xFF66BB6A),
//                focusedTextColor = Color.White,
//                unfocusedTextColor = Color.White,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                focusedLabelColor = Color.White,
//                unfocusedLabelColor = Color.White
//            ),
//            shape = RoundedCornerShape(30.dp)
//        )
        // Login Button
//        Button(
//            onClick = {
//                val users = loadUsers(context)
//                val user = users.find { it.email == email && it.password == password }
//                if (user != null) {
//                    navController.navigate("opening")
//                } else {
//                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .padding(vertical = 8.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
//            shape = RoundedCornerShape(30.dp)
//        ) {
//            Text(text = "Login", color = Color.White, fontWeight = FontWeight.Bold)
//        }



        FullWidthButton(
            label = "Login",
            onClick = {

            },
            color = greenDark
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

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        FullWidthButton(
            label = "Create Account",
            onClick = {
                navController.navigate("register")
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