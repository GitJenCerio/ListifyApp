package com.example.listifyoffline
import com.example.listifyoffline.MainActivity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.listifyoffline.ui.theme.ListifyOfflineTheme

class MainActivity : ComponentActivity() {

    private lateinit var offlineTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        offlineTextView = findViewById(R.id.textView)


        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)


        button1.setOnClickListener {
            showDialogue("Items Added")
        }
        button2.setOnClickListener {
            showDialogue("Changes Saved")
        }
        button3.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showDialogue(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success!")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

private fun showConfirmationDialog() {
    val context = this@MainActivity
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Confirmation")
    builder.setMessage("Are you sure you want to proceed?")
    builder.setPositiveButton("Yes")
    { _, _ ->

        Toast.makeText(context, "Items Discarded", Toast.LENGTH_SHORT).show()
    }
    builder.setNegativeButton("No") { dialog, _ ->

        dialog.dismiss()
    }
    builder.show()
}}


