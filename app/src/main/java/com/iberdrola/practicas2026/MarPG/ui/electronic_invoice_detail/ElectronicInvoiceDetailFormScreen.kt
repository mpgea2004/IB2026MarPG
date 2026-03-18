package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import android.R.attr.top
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ElectronicInvoiceDetailFormScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    ElectronicInvoiceDetailFormContent(
        state = state,
        events = viewModel.events,
        onBack = onBack,
        onNext = onNext,
        isButtonEnabled = viewModel.canContinue() // Usamos la validación del ViewModel
    )
}

@Composable
fun ElectronicInvoiceDetailFormContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isButtonEnabled: Boolean
) {
    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            ElectronicInvoiceHeader(
                title = "Activa tu factura electrónica",
                step = 1,
                onClose = onBack
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().background(WhiteApp).padding(bottom = 42.dp)) {
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Anterior
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, GreenDarkIberdrola),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDarkIberdrola)
                    ) {
                        Text("Anterior", fontWeight = FontWeight.Bold)
                    }

                    // Botón Siguiente
                    Button(
                        onClick = onNext,
                        enabled = isButtonEnabled,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D5C4C),
                            disabledContainerColor = Color(0xFFE0E0E0)
                        )
                    ) {
                        Text("Siguiente", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Email actual (Ofuscado)
            Text("Email vinculado a tu cuenta:", fontSize = 12.sp, color = Color.Gray)
            Text("a*****a@a.com", fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Formulario
            Text("¿En qué email deseas recibir tus facturas?", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("* Email", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Información básica sobre protección de datos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Bloque de Protección de Datos
            val proteccionDatosText = buildAnnotatedString {
                append("Responsable: Iberdrola Clientes S.A.U. ")
                withLink(LinkAnnotation.Clickable("responsable") { /* Aquí irá el Snackbar */ }) {
                    withStyle(
                        SpanStyle(
                            color = GreenDarkIberdrola,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Más info")
                    }
                }

                append("\n\nFinalidad: Gestión de la factura electrónica. ")
                withLink(LinkAnnotation.Clickable("finalidad") { /* Aquí irá el Snackbar */ }) {
                    withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) {
                        append("Más info")
                    }
                }

                append("\n\nDerechos: Acceso, rectificación, supresión, limitación del tratamiento, portabilidad de datos u oposición, incluida la oposición a decisiones individuales automatizadas. ")
                withLink(LinkAnnotation.Clickable("derechos") { /* Aquí irá el Snackbar */ }) {
                    withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) {
                        append("Más info")
                    }
                }
            }

            Text(
                text = proteccionDatosText,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            val checkboxText = buildAnnotatedString {
                append("He leído y acepto la Política de privacidad, acepto las ")

                withLink(LinkAnnotation.Clickable("condiciones") { /* Aquí irá el Snackbar */ }) {
                    withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) {
                        append("Condiciones Generales")
                    }
                }

                append(" y Particulares de la oferta y la suscripción a Factura Electrónica.")
            }

            // 5. Checkbox Legal
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = state.isLegalAccepted,
                    onCheckedChange = events.onLegalCheckChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenDarkIberdrola,
                        uncheckedColor = GreenDarkIberdrola,
                        checkmarkColor = GreenIberdrola
                    ),
                    modifier = Modifier.padding(end = 0.dp)
                )
                Text(
                    text = checkboxText,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Formulario - Estado Inicial")
@Composable
fun ElectronicInvoiceDetailFormPreview() {
    val mockContract = ElectronicInvoice(
        id = "12345",
        type = ContractType.LUZ,
        isEnabled = false, // Caso de activación
        email = "pepe2@gmail.com"
    )

    val mockState = ElectronicInvoiceState(
        selectedContract = mockContract,
        emailInput = "",
        isLegalAccepted = false
    )

    MaterialTheme {
        ElectronicInvoiceDetailFormContent(
            state = mockState,
            events = ElectronicInvoiceEvents(),
            onBack = {},
            onNext = {},
            isButtonEnabled = false // Deshabilitado por defecto
        )
    }
}

@Preview(showBackground = true, name = "Formulario - Datos Rellenados")
@Composable
fun ElectronicInvoiceDetailFormFilledPreview() {
    val mockContract = ElectronicInvoice(
        id = "12345",
        type = ContractType.LUZ,
        isEnabled = false,
        email = "pepe2@gmail.com"
    )

    val mockState = ElectronicInvoiceState(
        selectedContract = mockContract,
        emailInput = "nuevo_email@gmail.com",
        isLegalAccepted = true
    )

    MaterialTheme {
        ElectronicInvoiceDetailFormContent(
            state = mockState,
            events = ElectronicInvoiceEvents(),
            onBack = {},
            onNext = {},
            isButtonEnabled = true // Habilitado
        )
    }
}