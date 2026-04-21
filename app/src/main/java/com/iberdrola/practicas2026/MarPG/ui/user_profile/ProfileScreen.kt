package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.BackgroundApp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var showDiscardDialog by remember { mutableStateOf(false) }

    val handleBackAction = {
        if (!state.isSaved) {
            showDiscardDialog = true
        } else {
            onBack()
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
                    text = "¿Descartar cambios?",
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = "Tienes cambios sin guardar. ¿Estás seguro de que quieres salir y perder la información?",
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onBack()
                }) {
                    Text("Descartar", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancelar", color = GreenIberdrola)
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
                    text = "¿Cerrar sesión?",
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = GreenDarkIberdrola
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres cerrar sesión? Tendrás que volver a configurar tus datos la próxima vez.",
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.onDismissLogoutDialog()
                    viewModel.logout { onBack() } 
                }) {
                    Text("Cerrar sesión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissLogoutDialog() }) {
                    Text("Cancelar", color = Color.Gray)
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
        onBackClick = onBack,
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onLogout = { viewModel.onLogoutClick() }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_header_title),
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = IberPangeaFamily,
                        fontSize = 22.sp,
                        color = Color(0xFF333333)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBackAction,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.profile_header_back),
                            tint = GreenIberdrola,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    val isLoggedIn = state.name.isNotEmpty() || state.email.isNotEmpty() || state.password.isNotEmpty()
                    val canLogout = isLoggedIn && state.isSaved

                    IconButton(
                        onClick = events.onLogout,
                        enabled = canLogout,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = if (canLogout) Color(0xFFD32F2F) else Color.LightGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
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
            ProfileContent(
                state = state,
                events = events,
                modifier = Modifier.padding(padding)
            )
        }
    }
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

        ProfileAvatar()

        Spacer(modifier = Modifier.height(24.dp))
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
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { events.onSaveClick {
                // Navegamos atrás tras guardar con éxito
                events.onBackClick()
            } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenIberdrola,
                contentColor = WhiteApp
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = stringResource(R.string.profile_button_save), fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = IberPangeaFamily,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
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
