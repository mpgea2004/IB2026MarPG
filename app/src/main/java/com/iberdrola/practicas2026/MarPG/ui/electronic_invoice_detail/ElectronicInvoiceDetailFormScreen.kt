package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
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
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils

@Composable
fun ElectronicInvoiceDetailFormScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    // Eliminamos el remember para que se evalúe en cada recomposición
    val isButtonEnabled = viewModel.canContinue()

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onLegalCheckChange = { viewModel.onLegalAccepted(it) },
        onBack = onBack,
        onNext = onNext
    )

    ElectronicInvoiceDetailFormContent(
        state = state,
        events = events,
        isButtonEnabled = isButtonEnabled
    )
}

@Composable
fun ElectronicInvoiceDetailFormContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean
) {
    val emailActualOfuscado = state.selectedContract?.email?.let {
        if (it.isNotEmpty()) EmailUtils.obfuscateEmail(it) else "Sin email asignado"
    } ?: "Sin email asignado"

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            ElectronicInvoiceHeader(
                title = "Activa tu factura electrónica",
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Email vinculado a tu cuenta:", fontSize = 12.sp)
            Text(emailActualOfuscado, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Text("¿En qué email deseas recibir tus facturas?", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("* Email", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenDarkIberdrola,
                    cursorColor = GreenDarkIberdrola
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Información básica sobre protección de datos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            val proteccionDatosText = buildAnnotatedString {
                append("Responsable: Iberdrola Clientes S.A.U. ")
                withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) { append("Más info") }
                append("\n\nFinalidad: Gestión de la factura electrónica. ")
                withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) { append("Más info") }
                append("\n\nDerechos: Acceso, rectificación, supresión y otros derechos. ")
                withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) { append("Más info") }
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
                withStyle(SpanStyle(color = GreenDarkIberdrola, textDecoration = TextDecoration.Underline)) {
                    append("Condiciones Generales")
                }
                append(" y Particulares de la oferta y la suscripción a Factura Electrónica.")
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = state.isLegalAccepted,
                    onCheckedChange = { events.onLegalCheckChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenDarkIberdrola,
                        uncheckedColor = GreenDarkIberdrola,
                        checkmarkColor = Color.White
                    )
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