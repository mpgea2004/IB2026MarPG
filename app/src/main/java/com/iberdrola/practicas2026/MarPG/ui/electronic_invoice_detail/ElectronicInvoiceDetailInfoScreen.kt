package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import kotlinx.coroutines.delay

@Composable
fun ElectronicInvoiceDetailInfoScreen(
    viewModel: ElectronicInvoiceViewModel,
    electronicInvoice: ElectronicInvoice?,
    onBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToSuccess: () -> Unit
) {
    if (electronicInvoice == null) return

    val state = viewModel.state
    val haptic = LocalHapticFeedback.current
    
    var isNavigating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
    }

    val handleBack = {
        if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }

    BackHandler(enabled = true) { handleBack() }

    LaunchedEffect(electronicInvoice.id) {
        viewModel.selectContract(electronicInvoice)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onNavigateToSuccess()
        }
    }

    val events = ElectronicInvoiceEvents(
        onBack = handleBack,
        onNext = {
            if (!isNavigating) {
                isNavigating = true
                viewModel.onEmailChanged(electronicInvoice.email!!)
                onNavigateToEdit()
            }
        },
        onConfirmDeactivate = { viewModel.onDeactivateClick() }
    )

    if (state.showDeactivationConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDeactivationDialog() },
            icon = {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = GreenDarkIberdrola, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(text = stringResource(R.string.invoice_detail_dialog_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GreenDarkIberdrola)
            },
            text = {
                Text(text = stringResource(R.string.invoice_detail_dialog_text), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.closeDeactivationDialog()
                    viewModel.performDeactivate() 
                }) {
                    Text(stringResource(R.string.invoice_detail_dialog_confirm), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDeactivationDialog() }) {
                    Text(stringResource(R.string.invoice_detail_dialog_cancel), color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (state.showNoAddressDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeAddressDialog() },
            icon = { Icon(imageVector = Icons.Outlined.Info, contentDescription = null, tint = GreenDarkIberdrola, modifier = Modifier.size(32.dp)) },
            title = { Text(text = "Dirección de envío", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GreenDarkIberdrola) },
            text = {
                Column {
                    Text(text = "Introduce la dirección donde deseas recibir tus facturas en papel y confirma con tu contraseña:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = state.newAddressInput,
                        onValueChange = { viewModel.onNewAddressChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dirección completa", fontSize = 14.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black  ,
                            unfocusedTextColor = if (state.newAddressInput.isEmpty()) Color.Gray else Color.Black,

                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,

                            focusedIndicatorColor = GreenDarkIberdrola,
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = GreenDarkIberdrola,

                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = state.passwordInput,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.security_dialog_password_label), fontSize = 14.sp) },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, modifier = Modifier.size(24.dp))
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black  ,
                            unfocusedTextColor = if (state.newAddressInput.isEmpty()) Color.Gray else Color.Black,

                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,

                            focusedIndicatorColor = GreenDarkIberdrola,
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = GreenDarkIberdrola,

                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        singleLine = true
                    )
                    if (state.error != null) {
                        Text(text = stringResource(state.error), color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onDeactivateWithAddress(state.newAddressInput) },
                    enabled = state.newAddressInput.isNotEmpty() && state.passwordInput.isNotEmpty() && !state.isLoading
                ) {
                    Text("Confirmar", color = GreenDarkIberdrola, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeAddressDialog() }) {
                    Text(stringResource(R.string.security_dialog_cancel), color = Color.Gray)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    ElectronicInvoiceDetailInfoScreenContent(
        state = state,
        events = events,
        email = electronicInvoice.email
    )
}

@Composable
fun ElectronicInvoiceDetailInfoScreenContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    email: String?

) {
    val contract = state.selectedContract

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack = events.onBack)
        },
        bottomBar = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhiteApp)
            ) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Button(
                    onClick = events.onConfirmDeactivate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhiteApp,
                        contentColor = GreenDarkIberdrola
                    ),
                    border = BorderStroke(1.5.dp, color = GreenDarkIberdrola)
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null,tint = GreenDarkIberdrola,)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.invoice_detail_btn_deactivate), fontSize = 15.sp, color = GreenDarkIberdrola)
                }

                Button(
                    onClick = events.onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola, contentColor = WhiteApp)
                ) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.invoice_detail_btn_edit), fontSize = 15.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = if (contract?.type == ContractType.LUZ) stringResource(R.string.invoice_detail_type_light) else stringResource(R.string.invoice_detail_type_gas),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                fontFamily = IberPangeaFamily
            )

            Text(
                text = state.userProfile.address.ifEmpty { stringResource(R.string.profile_empty_address) },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.invoice_detail_current_status_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                fontSize = 11.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.invoice_detail_email_label),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = email ?: stringResource(R.string.invoice_detail_email_label),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp),
                thickness = DividerDefaults.Thickness,
                color = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.invoice_detail_info_box_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    lineHeight = 18.sp,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElectronicInvoiceDetailInfoPreview() {
    val mockContract = ElectronicInvoice(
        id = "12345",
        type = ContractType.LUZ,
        isEnabled = true,
        email = "pepe2@gmail.com"
    )
    val mockState = ElectronicInvoiceState(selectedContract = mockContract)

    MaterialTheme {
        ElectronicInvoiceDetailInfoScreenContent(
            state = mockState,
            events = ElectronicInvoiceEvents(),
            email = "pepe2@gmail.com"
        )
    }
}