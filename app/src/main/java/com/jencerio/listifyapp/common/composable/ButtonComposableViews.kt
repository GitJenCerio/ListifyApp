package com.jencerio.listifyapp.common.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jencerio.listifyapp.ui.theme.greenDark
import com.jencerio.listifyapp.ui.theme.primary

@Composable
fun FullWidthButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(), // Button occupies full width
        contentPadding = PaddingValues(vertical = 12.dp), // Padding for better spacing inside the button,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = color
        ),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium, // Apply titleMedium style or any other style
            color = primary
        )
    }
}

@Preview
@Composable
fun PreviewFullWidthButton() {
    Surface(
        color = primary // Set the background color for the preview
    ) {
        FullWidthButton(
            label = "Forgot Password", // Button label
            onClick = { /* Handle the button click here */ },
            color = greenDark
        )
    }
}
