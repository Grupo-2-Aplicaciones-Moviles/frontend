package weTech.weRide.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import weTech.weRide.ui.theme.WeRideTheme

/**
 * WeRide Text Field
 * Custom styled text input field
 */
@Composable
fun WeRideTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    focusRequester: FocusRequester? = null,
    onClick: (() -> Unit)? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(!isPassword) }

    val visualTransformation = if (isPassword && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(if (isError) 80.dp else 64.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        label = label?.let { { Text(it, fontSize = 14.sp) } },
        placeholder = placeholder?.let { { Text(it, fontSize = 14.sp) } },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp)
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            trailingIcon?.let { icon ->
                {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onAny = { onImeAction() }
        ),
        isError = isError,
        enabled = enabled,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        interactionSource = remember { MutableInteractionSource() }
    )

    if (isError && errorMessage != null) {
        Box(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * WeRide Phone Field
 * Optimized for phone number input
 */
@Composable
fun WeRidePhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    countryCode: String = "+51",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    focusRequester: FocusRequester? = null
) {
    WeRideTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = "999 999 999",
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Done,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
        focusRequester = focusRequester
    )
}

/**
 * WeRide Email Field
 * Optimized for email input
 */
@Composable
fun WeRideEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    placeholder: String = "your.email@example.com",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    WeRideTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled
    )
}

/**
 * WeRide Password Field
 * Optimized for password input with visibility toggle
 */
@Composable
fun WeRidePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "••••••••",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    WeRideTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        isPassword = true,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onImeAction = onImeAction,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled
    )
}

@Preview(showBackground = true)
@Composable
fun WeRideTextFieldPreview() {
    WeRideTheme {
        WeRideTextField(
            value = "",
            onValueChange = { },
            label = "Email",
            placeholder = "your.email@example.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeRidePasswordFieldPreview() {
    WeRideTheme {
        WeRidePasswordField(
            value = "",
            onValueChange = { },
            label = "Password",
            placeholder = "••••••••"
        )
    }
}
