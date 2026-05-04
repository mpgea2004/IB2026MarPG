package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.coroutines.delay

@Composable
fun ElectronicInvoiceEditEmailScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onCloseToHome: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state
    var showDiscardDialog by remember { mutableStateOf(false) }
    
    var isNavigating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
    }

    val handleBackAction = {
        val hasChanges = state.emailInput != (state.selectedContract?.email ?: "")
        if (hasChanges) {
            showDiscardDialog = true
        } else if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }

    val handleClose = {
        val hasChanges = state.emailInput != (state.selectedContract?.email ?: "")
        if (hasChanges) {
            showDiscardDialog = true
        } else if (!isNavigating) {
            isNavigating = true
            onCloseToHome()
        }
    }

    BackHandler(enabled = !showDiscardDialog) { handleBackAction() }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.form_discard_changes_title), fontFamily = IberPangeaFamily, fontWeight = FontWeight.Bold, color = GreenDarkIberdrola) },
            text = { Text(stringResource(R.string.edit_email_discard_message), fontFamily = IberPangeaFamily, color = Color.Black) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onCloseToHome()
                }) {
                    Text(stringResource(R.string.profile_discard_button), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.common_cancel), color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (state.showNoPhoneDialog) {
        SecurityPhoneDialog(state, viewModel, onNext)
    }
    
    if (state.showSameEmailWarning) {
        WarningSameEmailDialog(viewModel = viewModel)
    }

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onBack = handleBackAction,
        onNext = {
            viewModel.onContinueClick {
                if (!isNavigating) {
                    isNavigating = true
                    onNext()
                }
            }
        },
        onClose = handleClose
    )

    ElectronicInvoiceEditEmailContent(
        state = state,
        events = events,
        isButtonEnabled = viewModel.canContinue()
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
                title = stringResource(R.string.edit_email_header_title),
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AnimateEditEmailItem(index = 0) {
                Text(
                    text = stringResource(R.string.edit_email_question),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF222222),
                    fontFamily = IberPangeaFamily
                )
            }

            AnimateEditEmailItem(index = 1) {
                TextField(
                    value = state.emailInput,
                    onValueChange = events.onEmailChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontFamily = IberPangeaFamily
                    ),
                    placeholder = {
                        Text(stringResource(R.string.edit_email_placeholder), fontSize = 14.sp, color = Color.Gray, fontFamily = IberPangeaFamily)
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        focusedIndicatorColor = GreenDarkIberdrola,
                        cursorColor = GreenDarkIberdrola
                    ),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun AnimateEditEmailItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 100))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 100)),
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
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