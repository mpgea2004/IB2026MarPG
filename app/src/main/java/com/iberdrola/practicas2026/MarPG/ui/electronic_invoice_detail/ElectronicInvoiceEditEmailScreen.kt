package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.permissions.AppPermissions
import com.iberdrola.practicas2026.MarPG.ui.components.IberdrolaTextField
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceBottomBar
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ElectronicInvoiceHeader
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.SecurityPhoneDialog
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.WarningSameEmailDialog
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils
import com.iberdrola.practicas2026.MarPG.ui.utils.rememberPermissionsLauncher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ElectronicInvoiceEditEmailScreen(
    viewModel: ElectronicInvoiceViewModel,
    onBack: () -> Unit,
    onCloseToHome: () -> Unit,
    onNext: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var showDiscardDialog by remember { mutableStateOf(false) }
    var showPermissionErrorDialog by remember { mutableStateOf(false) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }
    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
        showPermissionErrorDialog = false
    }

    val requestPermissionThenNavigate = rememberPermissionsLauncher(
        permissions = listOf(AppPermissions.Notifications),
        onAllGranted = {
            if (!isNavigating) {
                isNavigating = true
                onNext()
            }
        },
        onDenied = { deniedList ->
            val rationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, android.Manifest.permission.POST_NOTIFICATIONS)
            } ?: true

            isPermanentlyDenied = !rationale
            showPermissionErrorDialog = true
            isNavigating = false
        }
    )

    val isInteractionEnabled = !isNavigating && !state.isLoading && !state.showNoAttemptsDialog && !state.showNoPhoneDialog && !state.showSameEmailWarning && !showDiscardDialog && !showPermissionErrorDialog

    val handleBackAction = {
        if (isInteractionEnabled) {
            val hasChanges = state.emailInput != (state.selectedContract?.email ?: "")
            if (hasChanges) {
                showDiscardDialog = true
            } else {
                isNavigating = true
                onBack()
            }
        }
    }

    val handleClose = {
        if (isInteractionEnabled) {
            val hasChanges = state.emailInput != (state.selectedContract?.email ?: "")
            if (hasChanges) {
                showDiscardDialog = true
            } else {
                isNavigating = true
                onCloseToHome()
            }
        }
    }

    BackHandler(enabled = !showDiscardDialog && !state.showNoAttemptsDialog && !state.showNoPhoneDialog && !state.showSameEmailWarning) {
        handleBackAction()
    }

    if (state.showNoAttemptsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeNoAttemptsDialog() },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.warning_same_email_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.otp_no_attempts_left),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeNoAttemptsDialog() }) {
                    Text(stringResource(R.string.common_ok), color = GreenDarkIberdrola, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.form_discard_changes_title), fontFamily = IberPangeaFamily, fontWeight = FontWeight.Bold, color = GreenDarkIberdrola) },
            text = { Text(stringResource(R.string.edit_email_discard_message), fontFamily = IberPangeaFamily, color = Color.Black) },
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
                    Text(stringResource(R.string.common_cancel), color = GreenDarkIberdrola)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showPermissionErrorDialog) {
        AlertDialog(
            onDismissRequest = {  },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
            title = {
                Text(
                    text = stringResource(if (isPermanentlyDenied) R.string.permission_blocked_title else R.string.permission_needed_title),
                    fontWeight = FontWeight.Bold,
                    fontFamily = IberPangeaFamily,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = stringResource(if (isPermanentlyDenied) R.string.permission_blocked_desc else R.string.permission_needed_desc),
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionErrorDialog = false
                    isNavigating = false

                    if (isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } else {
                        scope.launch {
                            delay(100)
                            requestPermissionThenNavigate()
                        }
                    }
                }) {
                    Text(
                        text = if (isPermanentlyDenied) stringResource(R.string.permission_go_to_settings) else stringResource(R.string.common_ok),
                        color = GreenDarkIberdrola,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionErrorDialog = false
                    if (!isNavigating) {
                        isNavigating = true
                        onBack()
                    }
                }) {
                    Text(stringResource(R.string.permission_back), color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (state.showNoPhoneDialog) {
        SecurityPhoneDialog(state, viewModel, {
            requestPermissionThenNavigate()
        })
    }
    
    if (state.showSameEmailWarning) {
        WarningSameEmailDialog(viewModel = viewModel)
    }

    val isButtonEnabled = viewModel.canContinue()

    val events = ElectronicInvoiceEvents(
        onEmailChange = { viewModel.onEmailChanged(it) },
        onBack = handleBackAction,
        onClose = handleClose,
        onNext = {
            if (isInteractionEnabled && isButtonEnabled) {
                viewModel.onContinueClick {
                    requestPermissionThenNavigate()
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        events.onViewScreen("Formulario_Editar_Email_Mar")
    }

    ElectronicInvoiceEditEmailContent(
        state = state,
        events = events,
        isButtonEnabled = isButtonEnabled && isInteractionEnabled,
        isInteractionEnabled = isInteractionEnabled
    )
}

@Composable
fun ElectronicInvoiceEditEmailContent(
    state: ElectronicInvoiceState,
    events: ElectronicInvoiceEvents,
    isButtonEnabled: Boolean = false,
    isInteractionEnabled: Boolean = true
) {
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

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
                isNextEnabled = isButtonEnabled,
                onNext = {
                    if (isButtonEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        events.onNext()
                    }
                },
                isBackEnabled = isInteractionEnabled
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
                val isEmailValid = EmailUtils.isValidEmail(state.emailInput)
                IberdrolaTextField(
                    value = state.emailInput,
                    onValueChange = events.onEmailChange,
                    label = stringResource(R.string.edit_email_placeholder),
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = isInteractionEnabled,
                    isError = state.emailInput.isNotEmpty() && !isEmailValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isButtonEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                events.onNext()
                            } else {
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    supportingText = {
                        if (state.emailInput.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.form_email_format_hint),
                                fontSize = 12.sp,
                                color = if (isEmailValid) Color.Gray else Color.Red,
                                fontFamily = IberPangeaFamily
                            )
                        }
                    }
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