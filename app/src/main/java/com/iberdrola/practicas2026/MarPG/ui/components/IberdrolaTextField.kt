package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IberdrolaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    isError: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    required: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textColor = when {
        !enabled -> Color.Gray.copy(alpha = 0.6f)
        isError -> Color.Red
        isFocused -> Color.Black
        else -> Color.Gray
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            color = textColor,
            fontSize = 16.sp,
            fontFamily = IberPangeaFamily
        ),
        cursorBrush = SolidColor(GreenDarkIberdrola),
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = isError,
                label = {
                    Text(
                        text = if (required) "* $label" else label,
                        fontFamily = IberPangeaFamily,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = trailingIcon,
                leadingIcon = leadingIcon,
                supportingText = supportingText,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray,
                    disabledTextColor = Color.Gray.copy(alpha = 0.6f),
                    errorTextColor = Color.Red,
                    focusedIndicatorColor = GreenDarkIberdrola,
                    unfocusedIndicatorColor = Color.DarkGray,
                    disabledIndicatorColor = Color.Gray.copy(alpha = 0.2f),
                    errorIndicatorColor = Color.Red,
                    focusedLabelColor = GreenDarkIberdrola,
                    unfocusedLabelColor = Color.Gray,
                    disabledLabelColor = Color.Gray.copy(alpha = 0.4f),
                    errorLabelColor = Color.Red,
                    cursorColor = GreenDarkIberdrola,
                    errorCursorColor = Color.Red
                ),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp)
            )
        }
    )
}
