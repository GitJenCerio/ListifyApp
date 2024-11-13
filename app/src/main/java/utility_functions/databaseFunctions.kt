package utility_functions

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

data class User(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

fun loadUsers(context: Context): List<User> {
    val file = File(context.filesDir, "database.json")
    if (!file.exists()) {
        // Copy the initial database from assets to internal storage
        context.assets.open("database.json").use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    val jsonString: String
    try {
        jsonString = file.bufferedReader().use { it.readText() }
        Log.d("loadUsers", "jsonString: $jsonString")
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return emptyList()
    }

    val listUserType = object : TypeToken<List<User>>() {}.type
    return Gson().fromJson(jsonString, listUserType)
}



fun registerUser(context: Context, newUser: User) {
    val users = loadUsers(context).toMutableList()
    users.add(newUser)
    val jsonString = Gson().toJson(users)
    try {
        val file = File(context.filesDir, "database.json")
        file.bufferedWriter().use { it.write(jsonString) }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
    }
}