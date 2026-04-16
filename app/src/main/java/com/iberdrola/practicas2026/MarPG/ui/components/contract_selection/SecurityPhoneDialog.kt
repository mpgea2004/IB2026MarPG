package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceState
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceViewModel
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

@Composable
fun SecurityPhoneDialog(
    state: ElectronicInvoiceState,
    viewModel: ElectronicInvoiceViewModel,
    onConfirm: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { viewModel.closePhoneDialog() },
        title = { Text(text = stringResource(R.string.security_dialog_title), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(text = stringResource(R.string.security_dialog_description))
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = state.newPhoneInput,
                    onValueChange = { viewModel.onNewPhoneChanged(it) },
                    placeholder = { Text(stringResource(R.string.security_dialog_phone_label),fontFamily = IberPangeaFamily) },
                    textStyle = TextStyle(fontFamily = IberPangeaFamily, color = Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = GreenDarkIberdrola),
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = state.passwordInput,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text(stringResource(R.string.security_dialog_password_label),fontFamily = IberPangeaFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = IberPangeaFamily, color = Color.Black),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) stringResource(R.string.security_dialog_password_hide) else stringResource(R.string.security_dialog_password_show)

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = GreenDarkIberdrola,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )

                if (state.error != null) {
                    Text(
                        stringResource(state.error),
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp),
                        fontFamily = IberPangeaFamily
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.savePhoneAndContinue(onConfirm) },
                enabled = state.newPhoneInput.length >= 9 && state.passwordInput.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola)
            ) {
                Text(text = stringResource(R.string.security_dialog_confirm),fontFamily = IberPangeaFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.closePhoneDialog() }) {
                Text(stringResource(R.string.security_dialog_cancel), color = Color.Gray,fontFamily = IberPangeaFamily)
            }
        }
    )
}