package com.jencerio.listifyapp.common.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jencerio.listifyapp.ui.theme.greenDark

@Composable
fun ForgotPasswordText(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = "Forgot Password?",
        modifier = modifier
            .padding(top = 8.dp) // Optional: to add spacing
            .clickable { onClick() }, // Handle the click action,
        color = greenDark
    )
}

@Preview
@Composable
fun PreviewForgotPasswordText() {
    ForgotPasswordText(
        onClick = {
            // Handle forgot password click here
        }
    )
}