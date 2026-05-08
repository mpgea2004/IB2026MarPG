package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.IberdrolaTextField
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.LoadingOverlay
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.NotificationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ElectronicInvoiceOtpScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onCloseToHome: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val notificationHandler = remember { NotificationHandler(context) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    
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

    LaunchedEffect(state.showSimulatedNotification, state.simulatedOtpCode) {
        if (state.showSimulatedNotification && state.simulatedOtpCode.isNotEmpty()) {
            notificationHandler.showSimpleNotification(
                contentTitle = "Código de seguridad Iberdrola",
                contentText = "Tu código para la factura electrónica de ${state.selectedContract?.type} es: ${state.simulatedOtpCode}"
            )
        }
    }

    val handleBack = {
        if (!isNavigating && !state.isLoading) {
            isNavigating = true
            onBack()
        }
    }

    val handleClose = {
        if (!isNavigating && !state.isLoading) {
            showDiscardDialog = true
        }
    }

    BackHandler(enabled = !showDiscardDialog) { handleBack() }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.form_discard_changes_title), color = GreenDarkIberdrola, fontFamily = IberPangeaFamily, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.form_discard_changes_message), color = Color.Black, fontFamily = IberPangeaFamily) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    isNavigating = true
                    onCloseToHome()
                }) {
                    Text(stringResource(R.string.profile_discard_button), color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.security_dialog_cancel), color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

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
        onResendOtp = { 
            if (!state.isLoading && !isNavigating) {
                viewModel.onResendOtp() 
            }
        },
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
        isButtonEnabled = state.otpInput.length == 6,
        phoneToShow = phoneToShow,
        isNavigating = isNavigating
    )
}

@Composable
fun ElectronicInvoiceOtpContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean,
    phoneToShow: String,
    isNavigating: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    
    var bufferedHasAttempts by remember { mutableStateOf(true) }
    
    val containerScale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0f) }
    val iconScale = remember { Animatable(1f) }

    LaunchedEffect(state.isLoading, state.resendAttempts) {
        if (!state.isLoading) {
            val currentHasAttempts = state.resendAttempts > 0
            
            if (bufferedHasAttempts && !currentHasAttempts) {
                delay(300)
                bufferedHasAttempts = false
                
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                
                launch {
                    containerScale.animateTo(1.05f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                    containerScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                launch {
                    repeat(2) {
                        glowAlpha.animateTo(0.6f, tween(300))
                        glowAlpha.animateTo(0f, tween(300))
                    }
                }
                launch {
                    delay(200)
                    iconScale.animateTo(1.4f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                    iconScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
            } else {
                bufferedHasAttempts = currentHasAttempts
            }
        }
    }

    val isInteractionEnabled = !isNavigating && !state.isLoading

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
                    onNext = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        events.onNext()
                    },
                    isNextEnabled = isButtonEnabled && isInteractionEnabled,
                    isBackEnabled = isInteractionEnabled,
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
                        fontFamily = IberPangeaFamily,
                        color = Color.Black
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
                    IberdrolaTextField(
                        value = state.otpInput,
                        onValueChange = { 
                            if (it.length <= 6) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                events.onOtpChange(it) 
                            }
                        },
                        label = stringResource(R.string.otp_placeholder),
                        isError = state.error != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isButtonEnabled && isInteractionEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    focusManager.clearFocus()
                                    events.onNext()
                                } else {
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                    
                    if (state.error != null) {
                        Text(
                            text = stringResource(state.error),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontFamily = IberPangeaFamily,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimateElectronicOtpItem(index = 3) {
                    val backgroundColor by animateColorAsState(
                        targetValue = if (bufferedHasAttempts) Color(0xFFE3F2FD) else Color(0xFFFFEBEE),
                        animationSpec = tween(600),
                        label = "background"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (bufferedHasAttempts) Color(0xFF455A64) else Color(0xFFC62828),
                        animationSpec = tween(600),
                        label = "icon"
                    )

                    Surface(
                        color = backgroundColor,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = containerScale.value
                                scaleY = containerScale.value
                            }
                            .border(
                                width = 2.dp,
                                color = Color(0xFFC62828).copy(alpha = glowAlpha.value),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp
                                )
                            )
                            .animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (bufferedHasAttempts) Icons.Outlined.Info else Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale.value
                                        scaleY = iconScale.value
                                    }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            AnimatedContent(
                                targetState = bufferedHasAttempts,
                                transitionSpec = {
                                    (fadeIn(tween(400)) + scaleIn(initialScale = 0.8f))
                                        .togetherWith(fadeOut(tween(300)) + scaleOut(targetScale = 0.8f))
                                },
                                label = "content"
                            ) { targetHasAttempts ->
                                Column {
                                    Text(
                                        text = stringResource(R.string.otp_not_received_title),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (targetHasAttempts) Color(0xFF263238) else Color(0xFFB71C1C),
                                        fontFamily = IberPangeaFamily
                                    )
                                    
                                    val descriptionText = when {
                                        !targetHasAttempts -> stringResource(R.string.otp_no_attempts_left)
                                        state.resendAttempts >= 3 -> stringResource(R.string.otp_not_received_desc_initial)
                                        else -> stringResource(R.string.otp_not_received_desc, state.resendAttempts)
                                    }

                                    Text(
                                        text = descriptionText,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = if (targetHasAttempts) Color(0xFF455A64) else Color(0xFFD32F2F),
                                        modifier = Modifier.padding(top = 4.dp),
                                        fontFamily = IberPangeaFamily
                                    )
                                    if (targetHasAttempts) {
                                        Text(
                                            text = stringResource(R.string.otp_resend_link),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (!isInteractionEnabled) Color.Gray else GreenDarkIberdrola,
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable(enabled = isInteractionEnabled) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    events.onResendOtp()
                                                }
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
        }

        if (state.isLoading) {
            LoadingOverlay()
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
        Column {
            content()
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
