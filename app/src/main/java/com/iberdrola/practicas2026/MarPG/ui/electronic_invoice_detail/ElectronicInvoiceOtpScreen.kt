package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.LoadingOverlay
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily


@Composable
fun ElectronicInvoiceOtpScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onCloseToHome: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNext()
        }
    }

    val phoneToShow = remember(state.userProfile.phone) {
        val rawPhone = state.userProfile.phone
        if (rawPhone.length >= 3) {
            "******${rawPhone.takeLast(3)}"
        } else {
            "******"
        }
    }

    val events = ElectronicInvoiceEvents(
        onOtpChange = { viewModel.onOtpChanged(it) },
        onResendOtp = { viewModel.onResendOtp() },
        onCloseBanner = { viewModel.closeResendBanner() },
        onBack = onBack,
        onClose = onCloseToHome,
        onNext = { if (state.otpInput.length == 6) {
            viewModel.performUpdate()
        } }
    )

    ElectronicInvoiceOtpContent(
        state = state,
        events = events,
        isButtonEnabled = state.otpInput.length >= 6,
        phoneToShow = phoneToShow
    )
}

@Composable
fun ElectronicInvoiceOtpContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean,
    phoneToShow: String
) {


    val hasAttempts = state.resendAttempts > 0

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                ElectronicInvoiceHeader(
                    title = stringResource(R.string.otp_title),
                    step = 3,
                    onClose = events.onClose,
                    totalSteps = 4
                )
            },
            bottomBar = {
                ElectronicInvoiceBottomBar(
                    onBack = events.onBack,
                    onNext = events.onNext,
                    isNextEnabled = isButtonEnabled,
                    showBanner = state.showResendSuccess,
                    onCloseBanner = events.onCloseBanner
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
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.otp_input_header),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )

                Text(
                    text = stringResource(R.string.otp_description, phoneToShow),
                    fontSize = 12.sp,
                    color = Color.Black,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                TextField(
                    value = state.otpInput,
                    onValueChange = events.onOtpChange,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    placeholder = { Text(stringResource(R.string.otp_placeholder), fontSize = 14.sp, color = Color.DarkGray,style = MaterialTheme.typography.bodyLarge) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Gray,
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedIndicatorColor = GreenDarkIberdrola,
                        cursorColor = GreenDarkIberdrola
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    color = if (hasAttempts) Color(0xFFE3F2FD) else Color(0xFFFFEBEE),
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
                            tint = if (hasAttempts) Color(0xFF455A64) else Color(0xFFC62828),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.otp_not_received_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (hasAttempts) Color(0xFF263238) else Color(0xFFB71C1C)
                            )
                            Text(
                                text = if (hasAttempts)
                                    stringResource(R.string.otp_not_received_desc, state.resendAttempts)
                                else
                                    stringResource(R.string.otp_no_attempts_left),
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = if (hasAttempts) Color(0xFF455A64) else Color(0xFFD32F2F),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (hasAttempts) {
                                Text(
                                    text = stringResource(R.string.otp_resend_link),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDarkIberdrola,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { events.onResendOtp() }
                                        .padding(vertical = 2.dp)
                                )
                            }
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
            isButtonEnabled = false,
            phoneToShow = "******45",
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
            isButtonEnabled = false,
            phoneToShow = "******45",
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
            isButtonEnabled = false,
            phoneToShow = "******45",
        )
    }
}