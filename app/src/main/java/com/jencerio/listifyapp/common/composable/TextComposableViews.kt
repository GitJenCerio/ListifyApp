package com.jencerio.listifyapp.common.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@androidx.compose.runtime.Composable
fun TitleText(
    modifier: Modifier = Modifier.Companion,
    text: String,
    textAlign: TextAlign = TextAlign.Companion.Start
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@androidx.compose.runtime.Composable
fun MediumTitleText(
    modifier: Modifier = Modifier.Companion,
    text: String,
    textAlign: TextAlign = TextAlign.Companion.Start
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = textAlign
    )
}

@androidx.compose.runtime.Composable
fun ErrorTextInputField(
    modifier: Modifier = Modifier.Companion,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}