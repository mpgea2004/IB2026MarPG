package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun SecurityPhoneDialog(
    state: ElectronicInvoiceState,
    viewModel: ElectronicInvoiceViewModel,
    onConfirm: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val isButtonEnabled = state.newPhoneInput.length >= 9 && state.passwordInput.isNotEmpty()

    AlertDialog(
        onDismissRequest = { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.closePhoneDialog() 
        },
        containerColor = WhiteApp,
        title = { Text(text = stringResource(R.string.security_dialog_title), fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = IberPangeaFamily) },
        text = {
            Column {
                Text(text = stringResource(R.string.security_dialog_description),color = Color.Black, fontFamily = IberPangeaFamily)
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = state.newPhoneInput,
                    label = { Text(stringResource(R.string.security_dialog_phone_label),fontFamily = IberPangeaFamily, color = Color.Gray) },
                    onValueChange = { viewModel.onNewPhoneChanged(it) },
                    textStyle = TextStyle(fontFamily = IberPangeaFamily, color = Color.Black,fontSize = 16.sp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = GreenDarkIberdrola,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = GreenDarkIberdrola,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = GreenDarkIberdrola,
                        errorCursorColor = Color.Red
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = state.passwordInput,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text(stringResource(R.string.security_dialog_password_label),fontFamily = IberPangeaFamily, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = IberPangeaFamily, color = Color.Black, fontSize = 16.sp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isButtonEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.savePhoneAndContinue(onConfirm)
                            }
                        }
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) stringResource(R.string.security_dialog_password_hide) else stringResource(R.string.security_dialog_password_show)

                        IconButton(onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            passwordVisible = !passwordVisible 
                        }) {
                            Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = GreenDarkIberdrola,
                        unfocusedIndicatorColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = GreenDarkIberdrola,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = GreenDarkIberdrola,
                        errorCursorColor = Color.Red
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
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.savePhoneAndContinue(onConfirm) 
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDarkIberdrola,
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.security_dialog_confirm),fontFamily = IberPangeaFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.closePhoneDialog() 
            }) {
                Text(stringResource(R.string.security_dialog_cancel), color = Color.Gray,fontFamily = IberPangeaFamily)
            }
        }
    )
}