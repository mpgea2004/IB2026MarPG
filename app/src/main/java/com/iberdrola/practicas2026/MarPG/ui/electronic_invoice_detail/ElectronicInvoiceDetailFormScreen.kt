package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
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
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.WarningSameEmailDialog
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicInvoiceDetailFormScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onCloseToHome: () -> Unit,
) {
    val state = viewModel.state
    var showDiscardDialog by remember { mutableStateOf(false) }
    
    var isNavigating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
    }

    val handleBackAction = {
        val hasChanges = state.emailInput.isNotEmpty() || state.isLegalAccepted
        if (hasChanges) {
            showDiscardDialog = true
        } else if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }

    val handleClose = {
        val hasChanges = state.emailInput.isNotEmpty() || state.isLegalAccepted
        if (hasChanges) {
            showDiscardDialog = true
        } else if (!isNavigating) {
            isNavigating = true
            onCloseToHome()
        }
    }

    BackHandler(enabled = !showDiscardDialog && !state.showSameEmailWarning && !state.showNoPhoneDialog) {
        handleBackAction() 
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("¿Descartar cambios?", color = GreenDarkIberdrola, fontFamily = IberPangeaFamily, fontWeight = FontWeight.Bold) },
            text = { Text("Has empezado a completar los datos. ¿Estás seguro de que quieres salir y perder el progreso?", color = Color.Black, fontFamily = IberPangeaFamily) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onCloseToHome()
                }) {
                    Text("Descartar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancelar", color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    val isButtonEnabled = viewModel.canContinue()
    val sheetState = rememberModalBottomSheetState()

    if (state.showNoPhoneDialog) {
        SecurityPhoneDialog(state, viewModel, {
            if (!isNavigating) {
                isNavigating = true
                onNext()
            }
        })
    }

    
    if (state.showSameEmailWarning) {
        WarningSameEmailDialog(viewModel = viewModel)
    }

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onLegalCheckChange = { viewModel.onLegalAccepted(it) },
        onBack = handleBackAction,
        onClose = handleClose,
        onNext = {
            viewModel.onContinueClick {
                if (!isNavigating) {
                    isNavigating = true
                    onNext()
                }
            }
        },
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

            Text(stringResource(R.string.form_linked_email_label), fontSize = 12.sp, color = Color.Black)
            Text(emailActualOfuscado, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.form_email_question), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.Black)

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                placeholder = { Text(stringResource(R.string.form_email_placeholder), fontSize = 14.sp, color = Color.Gray)},
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.LightGray,
                    focusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenDarkIberdrola,
                    cursorColor = GreenDarkIberdrola
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.form_data_protection_title),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.Black
            )

            val proteccionDatosText = buildAnnotatedString {
                append(stringResource(R.string.form_legal_responsable))
                append(" ")
                appendLink(moreInfo) {
                    events.onShowLegal(legalTitleResp, legalContentResp)
                }

                append(stringResource(R.string.form_legal_finalidad))
                append(" ")
                appendLink(moreInfo) {
                    events.onShowLegal(legalTitleFin, legalContentFin)
                }

                append(stringResource(R.string.form_legal_derechos))
                append(" ")
                appendLink(moreInfo) {
                    events.onShowLegal(legalTitleDer, legalContentDer)
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
                append(" ")
                appendLink(stringResource(R.string.form_condiciones_generales)) {
                    events.onShowLegal(legalTitleGen, legalContentGen)
                }
                append(" ")
                appendLink(stringResource(R.string.form_condiciones_particulares)) {
                    events.onShowLegal(legalTitlePart, legalContentPart)
                }
                append(" ")
                append(stringResource(R.string.form_checkbox_suffix))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = state.isLegalAccepted,
                    onCheckedChange = { events.onLegalCheckChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = GreenDarkIberdrola,
                        uncheckedColor = GreenDarkIberdrola,
                        checkmarkColor = WhiteApp
                    )
                )
                Text(
                    text = checkboxText,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 4.dp, top = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            if (state.showLegalSheet) {
                ModalBottomSheet(
                    onDismissRequest = { events.onDismissLegal() },
                    sheetState = sheetState,
                    containerColor = WhiteApp
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
                            color = Color.Black,
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
