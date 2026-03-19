package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ElectronicInvoiceEditEmailScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onBack = onBack,
        onNext = onNext
    )

    ElectronicInvoiceEditEmailContent(
        state = state,
        events = events,
        isButtonEnabled = state.emailInput.isNotEmpty()
    )
}

@Composable
fun ElectronicInvoiceEditEmailContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean
) {
    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            ElectronicInvoiceHeader(
                title = "Modificar email",
                step = 1,
                onClose = events.onBack
            )
        },
        bottomBar = {
            ElectronicInvoiceBottomBar(
                onBack = events.onBack,
                onNext = events.onNext,
                isNextEnabled = isButtonEnabled
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¿En qué email deseas recibir tus facturas?",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = Color(0xFF222222)
            )

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                placeholder = {
                    Text("* Nuevo email", fontSize = 14.sp, color = Color.Gray)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.LightGray,
                    focusedIndicatorColor = GreenDarkIberdrola,
                    cursorColor = GreenDarkIberdrola
                ),
                singleLine = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElectronicInvoiceEditEmailPreview() {
    MaterialTheme {
        ElectronicInvoiceEditEmailContent(
            state = ElectronicInvoiceState(emailInput = ""),
            events = ElectronicInvoiceEvents(),
            isButtonEnabled = false
        )
    }
}