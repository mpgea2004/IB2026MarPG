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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.SecurityPhoneDialog
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicInvoiceDetailFormScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCloseToHome: () -> Unit,
) {
    val state = viewModel.state
    val isButtonEnabled = viewModel.canContinue()

    val sheetState = rememberModalBottomSheetState()

    if (state.showNoPhoneDialog) {
        SecurityPhoneDialog(state, viewModel, onNext)
    }

    LaunchedEffect(Unit) {
        viewModel.logAnalytics("view_screen", mapOf("screen_name" to "Formulario_Alta_Factura_Elec"))
    }

    val events = viewModel.events.copy(
        onBack = onBack,
        onClose = {
            viewModel.logAnalytics("form_abandoned", mapOf("step" to "details"))
            onCloseToHome()
        },
        onNext = {
            viewModel.onContinueClick(onNext)
        }
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
    isButtonEnabled: Boolean = false,
    sheetState: SheetState
) {
    val emailParaOfuscar = when {
        state.userProfile.email.isNotEmpty() -> state.userProfile.email
        !state.selectedContract?.email.isNullOrEmpty() -> state.selectedContract.email
        else -> ""
    }

    val emailActualOfuscado = if (emailParaOfuscar.isNotEmpty()) {
        EmailUtils.obfuscateEmail(emailParaOfuscar)
    } else {
        stringResource(R.string.form_no_email_assigned)
    }

    val moreInfo = stringResource(R.string.form_more_info)
    val legalTitleResp = stringResource(R.string.legal_title_responsable)
    val legalContentResp = stringResource(R.string.legal_content_responsable)
    val legalTitleFin = stringResource(R.string.legal_title_finalidad)
    val legalContentFin = stringResource(R.string.legal_content_finalidad)
    val legalTitleDer = stringResource(R.string.legal_title_derechos)
    val legalContentDer = stringResource(R.string.legal_content_derechos)
    val legalTitleGen = stringResource(R.string.legal_title_generales)
    val legalContentGen = stringResource(R.string.legal_content_generales)
    val legalTitlePart = stringResource(R.string.legal_title_particulares)
    val legalContentPart = stringResource(R.string.legal_content_particulares)

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            ElectronicInvoiceHeader(
                title = stringResource(R.string.form_header_title),
                step = 1,
                onClose = events.onClose
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

            Text(stringResource(R.string.form_linked_email_label), fontSize = 12.sp)
            Text(emailActualOfuscado, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.form_email_question), fontWeight = FontWeight.Bold, fontSize = 16.sp)

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text(stringResource(R.string.form_email_placeholder), fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenDarkIberdrola,
                    cursorColor = GreenDarkIberdrola
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.form_data_protection_title),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            val proteccionDatosText = buildAnnotatedString {
                append(stringResource(R.string.form_legal_responsable))
                appendLink(moreInfo) {
                    events.onShowLegal(
                        legalTitleResp,
                        legalContentResp
                    )
                }

                append(stringResource(R.string.form_legal_finalidad))
                appendLink(moreInfo) {
                    events.onShowLegal(
                        legalTitleFin,
                        legalContentFin
                    )
                }

                append(stringResource(R.string.form_legal_derechos))
                appendLink(moreInfo) {
                    events.onShowLegal(
                        legalTitleDer,
                        legalContentDer
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
                append(stringResource(R.string.form_checkbox_prefix))

                appendLink(stringResource(R.string.form_condiciones_generales)) {
                    events.onShowLegal(
                        legalTitleGen,
                        legalContentGen
                    )
                }

                appendLink(stringResource(R.string.form_condiciones_particulares)) {
                    events.onShowLegal(
                        legalTitlePart,
                        legalContentPart
                    )
                }

                append(stringResource(R.string.form_checkbox_suffix))
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
                ModalBottomSheet(
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