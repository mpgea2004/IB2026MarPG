package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.IberdrolaSnackbar
import com.iberdrola.practicas2026.MarPG.ui.components.profile.ShimmerProfile
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.theme.BackgroundApp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var showDiscardDialog by remember { mutableStateOf(false) }
    var isBackTriggeredDuringSave by remember { mutableStateOf(false) }

    val logoutTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val handleBackAction = {
        if (state.isSaving || state.isSaveClicked) {
            isBackTriggeredDuringSave = true
        } else if (state.isEditMode) {
            if (!state.isSaved) {
                showDiscardDialog = true
            } else {
                viewModel.onDiscardClick()
            }
        } else {
            onBack()
        }
    }

    LaunchedEffect(state.saveJustFinished) {
        if (state.saveJustFinished && isBackTriggeredDuringSave) {
            isBackTriggeredDuringSave = false
        }
    }

    BackHandler(enabled = true) {
        handleBackAction()
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.profile_discard_title),
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.profile_discard_text),
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    if (state.isEditMode) viewModel.onDiscardClick() else onBack()
                }) {
                    Text(
                        text = stringResource(R.string.profile_discard_button),
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontFamily = IberPangeaFamily,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(
                        text = stringResource(R.string.invoice_detail_dialog_cancel),
                        color = GreenIberdrola,
                        fontFamily = IberPangeaFamily,
                    )
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (state.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissLogoutDialog() },
            title = {
                Text(
                    text = stringResource(R.string.profile_logout_title),
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.profile_logout_text),
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDismissLogoutDialog()
                    viewModel.logout { onBack() }
                }) {
                    Text(
                        text = stringResource(R.string.profile_logout_button),
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontFamily = IberPangeaFamily,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissLogoutDialog() }) {
                    Text(
                        text = stringResource(R.string.invoice_detail_dialog_cancel),
                        color = Color.Gray,
                        fontFamily = IberPangeaFamily,
                    )
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (state.showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onSecurityDismiss() },
            title = {
                Text(
                    text = stringResource(R.string.security_dialog_title),
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Column {
                    Text(
                        text = "Introduce tu contraseña actual para realizar cambios en el perfil.",
                        fontFamily = IberPangeaFamily,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = state.securityPasswordInput,
                        onValueChange = { viewModel.onSecurityPasswordChanged(it) },
                        label = { Text("Contraseña", fontFamily = IberPangeaFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (state.isSecurityPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onToggleSecurityPasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.isSecurityPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint =  if (state.isSecurityPasswordVisible) Color.DarkGray else Color.Gray
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = state.securityPasswordError != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = WhiteApp,
                            unfocusedContainerColor = WhiteApp,
                            errorContainerColor = WhiteApp,
                            disabledContainerColor = WhiteApp,
                            focusedBorderColor = GreenIberdrola,
                            unfocusedBorderColor = Color.LightGray,
                            errorBorderColor = Color.Red,
                            disabledBorderColor = Color.LightGray,
                            focusedLabelColor = GreenIberdrola,
                            unfocusedLabelColor = Color.LightGray,
                            errorLabelColor = Color.Red,
                            disabledLabelColor = Color.LightGray,
                            cursorColor = GreenIberdrola,
                            errorCursorColor = Color.Red,
                            errorTextColor = Color.Red,
                            disabledTextColor = Color.Black
                        )
                    )
                    if (state.securityPasswordError != null) {
                        Text(
                            text = stringResource(state.securityPasswordError),
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                            fontFamily = IberPangeaFamily
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onSecurityConfirmClick() }) {
                    Text(
                        text = "Confirmar",
                        color = GreenIberdrola,
                        fontWeight = FontWeight.Bold,
                        fontFamily = IberPangeaFamily
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onSecurityDismiss() }) {
                    Text(
                        text = stringResource(R.string.invoice_detail_dialog_cancel),
                        color = Color.Gray,
                        fontFamily = IberPangeaFamily
                    )
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    val events = ProfileEvents(
        onNameChanged = { viewModel.onNameChange(it) },
        onEmailChanged = { viewModel.onEmailChange(it) },
        onPhoneChanged = { viewModel.onPhoneChange(it) },
        onAddressChanged = { viewModel.onAddressChanged(it) },
        onSaveClick = { onSuccess -> viewModel.saveChanges(onSuccess) },
        onBackClick = handleBackAction,
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onLogout = { viewModel.onLogoutClick() },
        onEditClick = { viewModel.onEditClick() },
        onDiscardClick = {
            if (state.isSaving || state.isSaveClicked) {
                isBackTriggeredDuringSave = true
            } else if (!state.isSaved) {
                showDiscardDialog = true
            } else {
                viewModel.onDiscardClick()
            }
        },
        onSecurityPasswordChanged = { viewModel.onSecurityPasswordChanged(it) },
        onSecurityConfirmClick = { viewModel.onSecurityConfirmClick() },
        onSecurityDismiss = { viewModel.onSecurityDismiss() },
        onToggleSecurityPasswordVisibility = { viewModel.onToggleSecurityPasswordVisibility() }
    )

    Scaffold(
        topBar = {
            Surface(
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .combinedClickable(
                                onClick = handleBackAction
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = GreenIberdrola,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.invoice_list_back),
                            color = GreenIberdrola,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            fontFamily = IberPangeaFamily
                        )
                    }

                    val isLoggedIn = state.name.isNotEmpty() || state.email.isNotEmpty() || state.password.isNotEmpty()
                    val canLogout = isLoggedIn && state.isSaved

                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            Surface(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.profile_logout_button),
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 12.sp,
                                )
                            }
                        },
                        state = logoutTooltipState
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .combinedClickable(
                                    onClick = { if (canLogout) events.onLogout() },
                                    onLongClick = {
                                        if (canLogout) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch { logoutTooltipState.show() }
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(R.string.profile_logout_button),
                                tint = if (canLogout) Color(0xFFD32F2F) else Color.LightGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!state.isLoading) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                    color = WhiteApp,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = 8.dp)
                    ) {
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))

                        AnimatedContent(
                            targetState = state.isEditMode,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(400)) togetherWith fadeOut(animationSpec = tween(400))
                            },
                            label = "bottomButtonTransition"
                        ) { isEditMode ->
                            if (isEditMode) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 20.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = events.onDiscardClick,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp),
                                        shape = RoundedCornerShape(28.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = WhiteApp,
                                            contentColor = GreenDarkIberdrola
                                        ),
                                        border = BorderStroke(1.5.dp, GreenDarkIberdrola)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Volver",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = IberPangeaFamily
                                        )
                                    }

                                    SmartSaveButton(
                                        isSaving = state.isSaving,
                                        isSaved = state.isSaved,
                                        saveJustFinished = state.saveJustFinished,
                                        onClick = {
                                            events.onSaveClick {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(context.getString(R.string.profile_save_success))
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .height(56.dp)
                                    )
                                }
                            } else {
                                Button(
                                    onClick = events.onEditClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 20.dp)
                                        .height(56.dp),
                                    enabled = !state.isVerifying,
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenIberdrola,
                                        contentColor = WhiteApp,
                                        disabledContainerColor = Color.Gray
                                    )
                                ) {
                                    if (state.isVerifying) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = WhiteApp,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.profile_button_edit),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = IberPangeaFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                IberdrolaSnackbar(snackbarData = data)
            }
        },
        containerColor = BackgroundApp
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WhiteApp, BackgroundApp)
                )
            )
        ) {
            if (state.isLoading) {
                ShimmerProfile(
                    brush = shimmerBrush(),
                    modifier = Modifier.padding(padding)
                )
            } else {
                ProfileContent(
                    state = state,
                    events = events,
                    modifier = Modifier.padding(padding)
                )
            }

            if (isBackTriggeredDuringSave && (state.isSaving || state.isSaveClicked)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = true, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = WhiteApp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = GreenIberdrola, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Guardando cambios...",
                                fontFamily = IberPangeaFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartSaveButton(
    isSaving: Boolean,
    isSaved: Boolean,
    saveJustFinished: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonScale by animateFloatAsState(
        targetValue = if (isSaving) 0.95f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "buttonScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shimmerTransition")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                -1f at 0
                -1f at 4000
                2f at 5000 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale)
            .then(
                if (!isSaved && !isSaving && !saveJustFinished) {
                    Modifier.drawWithContent {
                        drawContent()
                        val brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0f),
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0f)
                            ),
                            start = Offset(size.width * shimmerOffset, 0f),
                            end = Offset(size.width * (shimmerOffset + 0.5f), size.height)
                        )
                        drawRect(brush = brush)
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenIberdrola,
            contentColor = WhiteApp,
            disabledContainerColor = Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        enabled = !isSaving
    ) {
        AnimatedContent(
            targetState = when {
                isSaving -> ButtonState.Saving
                saveJustFinished -> ButtonState.Success
                else -> ButtonState.Idle
            },
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "buttonContent"
        ) { targetState ->
            when (targetState) {
                ButtonState.Idle -> {
                    Text(
                        text = stringResource(R.string.profile_button_save),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = IberPangeaFamily,
                        letterSpacing = 1.sp
                    )
                }
                ButtonState.Saving -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = WhiteApp,
                        strokeWidth = 2.dp
                    )
                }
                ButtonState.Success -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.profile_complete_description),
                        tint = WhiteApp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

enum class ButtonState {
    Idle, Saving, Success
}

@Composable
fun ProfileContent(
    state: ProfileState,
    events: ProfileEvents,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        AnimateProfileItem(index = 0) {
            Text(
                text = stringResource(R.string.profile_header_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimateProfileItem(index = 1) {
            ProfileAvatar()
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimateProfileItem(index = 2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteApp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 18.dp)
                                .background(GreenIberdrola, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.profile_section_account),
                            fontFamily = IberPangeaFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenIberdrola
                        )
                    }

                    AnimatedContent(
                        targetState = state.isEditMode,
                        transitionSpec = {
                            if (targetState) {
                                (slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(500)))
                                    .togetherWith(slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(500)))
                            } else {
                                (slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(500)))
                                    .togetherWith(slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(500)))
                            }.using(SizeTransform(clip = false))
                        },
                        label = "profileFieldsTransition"
                    ) { isEditMode ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (isEditMode) {
                                ProfileField(
                                    value = state.name,
                                    errorMessage = state.nameError,
                                    label = stringResource(R.string.profile_label_name),
                                    icon = Icons.Default.Person,
                                    onValueChange = events.onNameChanged
                                )

                                ProfileField(
                                    value = state.email,
                                    errorMessage = state.emailError,
                                    label = stringResource(R.string.profile_label_email),
                                    icon = Icons.Default.Email,
                                    keyboardType = KeyboardType.Email,
                                    onValueChange = events.onEmailChanged
                                )

                                ProfileField(
                                    value = state.phone,
                                    errorMessage = state.phoneError,
                                    label = stringResource(R.string.profile_label_phone),
                                    icon = Icons.Default.Phone,
                                    keyboardType = KeyboardType.Phone,
                                    onValueChange = events.onPhoneChanged
                                )

                                ProfileField(
                                    value = state.address,
                                    label = stringResource(R.string.profile_label_address),
                                    icon = Icons.Default.Home,
                                    onValueChange = events.onAddressChanged
                                )
                                PasswordField(
                                    value = state.password,
                                    errorMessage = state.passwordError,
                                    isVisible = passwordVisible,
                                    onValueChange = events.onPasswordChanged,
                                    onToggleVisibility = { passwordVisible = !passwordVisible }
                                )
                            } else {
                                ProfileInfoItem(
                                    label = stringResource(R.string.profile_label_name),
                                    value = state.name.ifEmpty { "No configurado" },
                                    icon = Icons.Default.Person
                                )
                                ProfileInfoItem(
                                    label = stringResource(R.string.profile_label_email),
                                    value = state.email.ifEmpty { "No configurado" },
                                    icon = Icons.Default.Email
                                )
                                ProfileInfoItem(
                                    label = stringResource(R.string.profile_label_phone),
                                    value = state.phone.ifEmpty { "No configurado" },
                                    icon = Icons.Default.Phone
                                )
                                ProfileInfoItem(
                                    label = stringResource(R.string.profile_label_address),
                                    value = state.address.ifEmpty { "No configurado" },
                                    icon = Icons.Default.Home
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreenIberdrola,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label.replace("*", "").trim(),
                style = MaterialTheme.typography.labelMedium,
                color = TextGrey,
                fontFamily = IberPangeaFamily
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
fun AnimateProfileItem(
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
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
    }
}

@Composable
fun ProfileAvatar() {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.size(110.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GreenIberdrola.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .border(1.dp, GreenIberdrola.copy(alpha = 0.1f), CircleShape)
        )
        Card(
            modifier = Modifier.size(90.dp) ,
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteApp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = GreenIberdrola
                )
            }
        }
    }
}

@Composable
fun PasswordField(
    value: String,
    errorMessage: Int?,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = IberPangeaFamily,
                color = Color(0xFF333333)
            ),
            label = {
                Text(
                    text = stringResource(R.string.profile_label_password),
                    fontFamily = IberPangeaFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = GreenIberdrola.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = image, null, tint = TextGrey, modifier = Modifier.size(20.dp))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = errorMessage != null,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF9FBF9),
                unfocusedContainerColor = Color.Transparent,
                cursorColor = GreenIberdrola,
                focusedBorderColor = GreenIberdrola,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = GreenIberdrola,
                unfocusedLabelColor = TextGrey,
                errorBorderColor = Color(0xFFD32F2F)
            )
        )
        if (errorMessage != null) {
            Text(
                text = stringResource(errorMessage),
                color = Color(0xFFD32F2F),
                fontFamily = IberPangeaFamily,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileField(
    value: String,
    label: String,
    icon: ImageVector,
    errorMessage: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = IberPangeaFamily,
                color = Color(0xFF333333)
            ),
            label = {
                Text(
                    text = label,
                    fontFamily = IberPangeaFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = { Icon(icon, null, tint = GreenIberdrola.copy(alpha = 0.7f), modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF9FBF9),
                unfocusedContainerColor = Color.Transparent,
                cursorColor = GreenIberdrola,
                focusedBorderColor = GreenIberdrola,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedLabelColor = GreenIberdrola,
                unfocusedLabelColor = TextGrey,
                errorBorderColor = Color(0xFFD32F2F)
            )
        )
        if (errorMessage != null) {
            Text(
                text = stringResource(errorMessage),
                color = Color(0xFFD32F2F),
                fontFamily = IberPangeaFamily,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}