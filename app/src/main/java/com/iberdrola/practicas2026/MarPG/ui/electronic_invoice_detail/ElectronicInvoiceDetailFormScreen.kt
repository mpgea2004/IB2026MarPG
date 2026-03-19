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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicInvoiceDetailFormScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    val isButtonEnabled = viewModel.canContinue()

    val sheetState = rememberModalBottomSheetState()

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onLegalCheckChange = { viewModel.onLegalAccepted(it) },
        onBack = onBack,
        onNext = onNext,
        onShowLegal = { title, content -> viewModel.onShowLegalDetail(title, content) },
        onDismissLegal = { viewModel.onDismissLegalSheet() }
    )

    ElectronicInvoiceDetailFormContent(
        state = state,
        events = events,
        isButtonEnabled = isButtonEnabled,
        sheetState = sheetState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicInvoiceDetailFormContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean,
    sheetState: SheetState
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
                appendLink("Más info") {
                    events.onShowLegal(
                        "Responsable del tratamiento",
                        "Iberdrola Clientes S.A.U., con domicilio social en Plaza Euskadi número 5, 48009 Bilbao, tratará sus datos en calidad de responsable para gestionar su relación contractual y la activación de servicios digitales."
                    )
                }

                append("\n\nFinalidad: Gestión de la factura electrónica. ")
                appendLink("Más info") {
                    events.onShowLegal(
                        "Finalidad del tratamiento",
                        "Sus datos serán tratados para la prestación del servicio de facturación electrónica, envío de avisos de disponibilidad de factura en su email y gestión del área de cliente, además del cumplimiento de obligaciones legales."
                    )
                }

                append("\n\nDerechos: Acceso, rectificación, supresión, limiteción del tratamineto, portabilidad de datos u oposición, incluida la oposición a decisiones individuales automatizadas.")
                appendLink("Más info") {
                    events.onShowLegal(
                        "Tus Derechos",
                        "Usted puede ejercer sus derechos de acceso, rectificación, supresión, limitación, portabilidad y oposición contactando con nuestro delegado de protección de datos a través del correo protecciondedatos@iberdrola.es."
                    )
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

                appendLink("Condiciones Generales") {
                    events.onShowLegal(
                        "Condiciones Generales",
                        "Al activar el servicio de Factura Electrónica, usted acepta dejar de recibir facturas en papel. Las facturas tendrán plena validez legal y podrá volver al sistema de papel en cualquier momento desde su área privada."
                    )
                }

                appendLink(" y Particulares") {
                    events.onShowLegal(
                        "Condiciones Particulares",
                        "Estas condiciones regulan las ofertas específicas asociadas a su contrato. El mantenimiento de ciertos descuentos puede estar vinculado a la permanencia en facturación electrónica."
                    )
                }

                append(" de la oferta y la suscripción a Factura Electrónica.")
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
            if (state.showLegalSheet) {
                androidx.compose.material3.ModalBottomSheet(
                    onDismissRequest = { events.onDismissLegal() },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 48.dp)
                    ) {
                        Text(
                            text = state.selectedLegalTitle ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            color = GreenDarkIberdrola,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.selectedLegalContent ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

fun AnnotatedString.Builder.appendLink(
    text: String,
    color: Color = GreenDarkIberdrola,
    onClick: () -> Unit
) {
    withLink(LinkAnnotation.Clickable(text) { onClick() }) {
        withStyle(
            SpanStyle(
                color = color,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold
            )
        ) {
            append(text)
        }
    }
}