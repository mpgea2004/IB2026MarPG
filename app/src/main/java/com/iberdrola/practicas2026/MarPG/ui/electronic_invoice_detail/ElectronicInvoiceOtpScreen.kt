package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import kotlinx.coroutines.delay


@Composable
fun ElectronicInvoiceOtpScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onCloseToHome: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state
    val haptic = LocalHapticFeedback.current
    
    var isNavigating by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        viewModel.clearOtp()
        onDispose {
            viewModel.clearOtp()
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
        viewModel.startOtpSimulation()
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

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            isNavigating = true
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
        onBack = handleBack,
        onClose = handleClose,
        onNext = { 
            if (state.otpInput.length == 6 && !isNavigating && !state.isLoading) {
                viewModel.verifyOtpAndPerformUpdate()
            }
        }
    )

    ElectronicInvoiceOtpContent(
        state = state,
        events = events,
        isButtonEnabled = state.otpInput.length >= 6,
        phoneToShow = phoneToShow,
        onDismissNotification = { viewModel.closeSimulatedNotification() }
    )
}

@Composable
fun ElectronicInvoiceOtpContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean,
    phoneToShow: String,
    onDismissNotification: () -> Unit = {}
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

                AnimateElectronicOtpItem(index = 0) {
                    Text(
                        text = stringResource(R.string.otp_input_header),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        fontFamily = IberPangeaFamily
                    )
                }

                AnimateElectronicOtpItem(index = 1) {
                    Text(
                        text = stringResource(R.string.otp_description, phoneToShow),
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 12.sp,
                        modifier = Modifier.padding(top = 16.dp),
                        fontFamily = IberPangeaFamily
                    )
                }

                AnimateElectronicOtpItem(index = 2) {
                    TextField(
                        value = state.otpInput,
                        onValueChange = events.onOtpChange,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        placeholder = { Text(stringResource(R.string.otp_placeholder), fontSize = 14.sp, color = Color.DarkGray,style = MaterialTheme.typography.bodyLarge, fontFamily = IberPangeaFamily) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontFamily = IberPangeaFamily
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.DarkGray,
                            focusedIndicatorColor = GreenDarkIberdrola,
                            cursorColor = GreenDarkIberdrola
                        ),
                        isError = state.error != null,
                        supportingText = {
                            if (state.error != null) {
                                Text(
                                    text = stringResource(state.error),
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontFamily = IberPangeaFamily
                                )
                            }
                        },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimateElectronicOtpItem(index = 3) {
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
                                    color = if (hasAttempts) Color(0xFF263238) else Color(0xFFB71C1C),
                                    fontFamily = IberPangeaFamily
                                )
                                Text(
                                    text = if (hasAttempts)
                                        stringResource(R.string.otp_not_received_desc, state.resendAttempts)
                                    else
                                        stringResource(R.string.otp_no_attempts_left),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = if (hasAttempts) Color(0xFF455A64) else Color(0xFFD32F2F),
                                    modifier = Modifier.padding(top = 4.dp),
                                    fontFamily = IberPangeaFamily
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
                                            .padding(vertical = 2.dp),
                                        fontFamily = IberPangeaFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.showSimulatedNotification,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it },
            exit = fadeOut(tween(500)) + slideOutVertically(tween(500)) { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            SimulatedNotification(
                message = state.simulatedNotificationMessage,
                onDismiss = onDismissNotification
            )
        }

        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun SimulatedNotification(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDismiss() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GreenIberdrola),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iberdrola),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "IBERDROLA",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ahora",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp,
                    fontFamily = IberPangeaFamily
                )
            }
        }
    }
}

@Composable
fun AnimateElectronicOtpItem(
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
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(500))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(500)),
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
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