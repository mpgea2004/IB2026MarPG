package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.LoadingOverlay
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola


@Composable
fun ElectronicInvoiceOtpScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    ElectronicInvoiceOtpContent(
        state = state,
        events = viewModel.events,
        onBack = onBack,
        onNext = onNext,
        //El botón se habilita si el código tiene 6 dígitos
        isButtonEnabled = state.otpInput.length >= 6
    )
}

@Composable
fun ElectronicInvoiceOtpContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isButtonEnabled: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                ElectronicInvoiceHeader(
                    title = "Activa tu factura electrónica",
                    step = 3,
                    onClose = onBack,
                    totalSteps = 4
                )
            },
            bottomBar = {
                ElectronicInvoiceBottomBar(
                    onBack = onBack,
                    onNext = onNext,
                    isNextEnabled = isButtonEnabled,
                    showBanner = state.showResendSuccess,
                    onCloseBanner = { /* evento para poner el booleano en false */ }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Introduce tu código de verificación",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = "Para verificar tu identidad, hemos enviado un código al teléfono ******146. Por favor, introdúcelo a continuación:",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Campo de entrada del código
                TextField(
                    value = state.otpInput,
                    onValueChange = events.onOtpChange,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    placeholder = { Text("* Código de verificación", fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedIndicatorColor = GreenDarkIberdrola
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // CUADRO AZUL (Info Box)
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color(0xFF455A64),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "¿No has recibido el código?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF263238)
                            )
                            Text(
                                text = "Si no lo encuentras, podemos volver a enviar el SMS. Recuerda que aún te quedan 2 intentos.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF455A64),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Volver a enviar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenDarkIberdrola,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable { events.onResendOtp() }
                            )
                        }
                    }
                }
            }
        }
        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ElectronicInvoiceOtpPreview() {
    MaterialTheme {
        ElectronicInvoiceOtpContent(
            state = ElectronicInvoiceState(otpInput = ""),
            events = ElectronicInvoiceEvents(),
            onBack = {},
            onNext = {},
            isButtonEnabled = false
        )
    }
}


@Preview(showBackground = true, name = "OTP - SMS Reenviado")
@Composable
fun ElectronicInvoiceOtpResentPreview() {
    MaterialTheme {
        ElectronicInvoiceOtpContent(
            state = ElectronicInvoiceState(
                otpInput = "123",
                showResendSuccess = true
            ),
            events = ElectronicInvoiceEvents(),
            onBack = {},
            onNext = {},
            isButtonEnabled = false
        )
    }
}
@Preview(showBackground = true, name = "OTP - Cargando")
@Composable
fun ElectronicInvoiceOtpLoadingPreview() {
    MaterialTheme {
        ElectronicInvoiceOtpContent(
            state = ElectronicInvoiceState(
                isLoading = true
            ),
            events = ElectronicInvoiceEvents(),
            onBack = {},
            onNext = {},
            isButtonEnabled = false
        )
    }
}