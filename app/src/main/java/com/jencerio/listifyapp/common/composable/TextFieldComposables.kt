package com.jencerio.listifyapp.common.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.jencerio.listifyapp.R
import com.jencerio.listifyapp.ui.theme.primary

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier.Companion,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Companion.Done
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    var isPasswordVisible by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {

                val visibleIconAndText = kotlin.Pair(
                    first = androidx.compose.material.icons.Icons.Outlined.Visibility,
                    second = androidx.compose.ui.res.stringResource(id =R.string.icon_password_visible )
                )

                val hiddenIconAndText = kotlin.Pair(
                    first = androidx.compose.material.icons.Icons.Outlined.VisibilityOff,
                    second = androidx.compose.ui.res.stringResource(id = R.string.icon_password_hidden)
                )

                val passwordVisibilityIconAndText =
                    if (isPasswordVisible) visibleIconAndText else hiddenIconAndText

                // Render Icon
                androidx.compose.material3.Icon(
                    imageVector = passwordVisibilityIconAndText.first,
                    contentDescription = passwordVisibilityIconAndText.second
                )
            }
        },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.Companion.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Companion.Password,
            imeAction = imeAction
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        isError = isError,
        supportingText = {
            if (isError) {
                ErrorTextInputField(text = errorText)
            }
        }
    )
}

/**
 * Email Text Field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(), // This ensures the text field fills the width
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        isError = isError,
        supportingText = {
            if (isError) {
                ErrorTextInputField(text = errorText)
            }
        }
    )
}

/**
 * Mobile Number Text Field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileNumberTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Next
) {

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction
        ),
        isError = isError,
        supportingText = {
            if (isError) {
                ErrorTextInputField(text = errorText)
            }
        }
    )

}

@Preview
@Composable
fun PreviewEmailTextField() {
    Surface(
        color = primary // Set the background color for the preview
    ) {
        EmailTextField(
            value = "",
            onValueChange = {},
            label = "Email",
            isError = false,
            errorText = ""
        )
    }
}

@Preview
@Composable
fun PreviewPasswordTextField() {
    Surface(
        color = primary // Set the background color for the preview
    ) {
        PasswordTextField(
            value = "",
            onValueChange = {},
            label = "Password",
            isError = false,
            errorText = "",
        )
    }
}