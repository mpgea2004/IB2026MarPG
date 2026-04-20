package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
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

    val handleClose = {
        if (!isNavigating) {
            isNavigating = true
            onCloseToHome()
        }
    }

    BackHandler(enabled = true) { handleBack() }

    if (state.showNoPhoneDialog) {
        SecurityPhoneDialog(state, viewModel, onNext)
    }
    
    if (state.showSameEmailWarning) {
        WarningSameEmailDialog(viewModel = viewModel)
    }

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onBack = handleBack,
        onNext = {
            if (!isNavigating) {
                isNavigating = true
                viewModel.onContinueClick {
                    isNavigating = false
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

            Text(
                text = stringResource(R.string.edit_email_question),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color(0xFF222222)
            )

            TextField(
                value = state.emailInput,
                onValueChange = events.onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textStyle = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(stringResource(R.string.edit_email_placeholder), fontSize = 14.sp, color = Color.Gray)
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