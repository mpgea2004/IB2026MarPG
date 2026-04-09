package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state

    val events = ProfileEvents(
        onNameChanged = { viewModel.onNameChange(it) },
        onEmailChanged = { viewModel.onEmailChange(it)  },
        onPhoneChanged = { viewModel.onPhoneChange(it)  },
        onAddressChanged = {viewModel.onAddressChanged(it)},
        onSaveClick = { onSuccess -> viewModel.saveChanges(onSuccess) },
        onBackClick = onBack,
        onPasswordChanged = {viewModel.onPasswordChanged(it)}
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile_header_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.profile_header_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteApp,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        },
        containerColor = WhiteApp
    ) { padding ->
        ProfileContent(
            state = state,
            events = events,
            modifier = Modifier.padding(padding)
        )
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
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        ProfileAvatar()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.profile_section_account),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileField(
                value = state.name,
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

            OutlinedTextField(
                value = state.password,
                onValueChange = { events.onPasswordChanged.invoke(it) },
                label = { Text(stringResource(R.string.profile_label_password)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenDarkIberdrola) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDarkIberdrola,
                    focusedLabelColor = GreenDarkIberdrola,
                    cursorColor = GreenDarkIberdrola
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                events.onSaveClick {
                    events.onBackClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(stringResource(R.string.profile_button_save), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ProfileAvatar() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(GreenDarkIberdrola.copy(alpha = 0.1f), CircleShape)
            .border(2.dp, GreenDarkIberdrola.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = GreenDarkIberdrola
        )
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
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = GreenDarkIberdrola) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenDarkIberdrola,
                focusedLabelColor = GreenDarkIberdrola,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        if (errorMessage != null) {
            Text(
                text = stringResource(errorMessage),
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
