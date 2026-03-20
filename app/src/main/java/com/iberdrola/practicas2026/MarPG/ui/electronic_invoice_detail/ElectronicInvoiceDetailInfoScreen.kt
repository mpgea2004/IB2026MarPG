package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

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

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(electronicInvoice.id) {
        viewModel.selectContract(electronicInvoice)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToSuccess()
        }
    }

    val events = ElectronicInvoiceEvents(
        onBack = onBack,
        onNext = {
            viewModel.onEmailChanged(electronicInvoice.email!!)
            onNavigateToEdit()
        },
        onConfirmDeactivate = { showDeleteDialog = true }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = GreenDarkIberdrola,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.invoice_detail_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.invoice_detail_dialog_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.performDeactivate()
                    }
                ) {
                    Text(stringResource(R.string.invoice_detail_dialog_confirm), color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(stringResource(R.string.invoice_detail_dialog_cancel), color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    ElectronicInvoiceDetailInfoContent(
        state = state,
        events = events,
        email = electronicInvoice.email
    )
}

@Composable
fun ElectronicInvoiceDetailInfoContent(
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
                        containerColor = Color.Transparent,
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
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola)
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
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = stringResource(R.string.invoice_detail_address),
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
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = email ?: stringResource(R.string.invoice_detail_email_label),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 24.dp),
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
                Spacer(modifier = Modifier.width(12.dp))
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
        ElectronicInvoiceDetailInfoContent(
            state = mockState,
            events = ElectronicInvoiceEvents(),
            email = "pepe2@gmail.com"
        )
    }
}