package com.iberdrola.practicas2026.MarPG.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.IberdrolaTextField
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import androidx.compose.ui.res.stringResource
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.logAnalytics(
            "view_screen",
            mapOf("screen_name" to "Login"),
            priority = AnalyticsPriority.LOW
        )
    }

    val events = LoginEvents(
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClick = { onSuccess -> viewModel.onLoginClick(onSuccess) },
        onRegisterClick = onNavigateToRegister,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
    )

    LoginContent(
        state = state,
        events = events,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    events: LoginEvents,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNavigating by remember { mutableStateOf(false) }

    val isInteractionEnabled = !isNavigating && !state.isLoading

    val handleRegisterNavigation = {
        if (isInteractionEnabled) {
            isNavigating = true
            events.onRegisterClick()
        }
    }

    val handleLogin = {
        if (isInteractionEnabled) {
            events.onLoginClick(onNavigateToHome)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = WhiteApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.iberdrola),
                contentDescription = stringResource(R.string.login_logo_description),
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.login_title),
                fontFamily = IberPangeaFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = GreenDarkIberdrola,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.login_subtitle),
                fontFamily = IberPangeaFamily,
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            IberdrolaTextField(
                value = state.email,
                onValueChange = events.onEmailChanged,
                label = stringResource(R.string.login_email_label),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(text = it, color = Color.Red, fontSize = 12.sp) } }
            )

            Spacer(modifier = Modifier.height(20.dp))

            IberdrolaTextField(
                value = state.password,
                onValueChange = events.onPasswordChanged,
                label = stringResource(R.string.login_password_label),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { handleLogin() }
                ),
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(text = it, color = Color.Red, fontSize = 12.sp) } },
                trailingIcon = {
                    val icon = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = events.onTogglePasswordVisibility) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(R.string.login_password_visibility),
                            modifier = Modifier.clip(RoundedCornerShape(50.dp)),
                            tint =  if (state.isPasswordVisible) Color.DarkGray else Color.Gray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = handleLogin,
                modifier = Modifier
                    .width(220.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola),
                enabled = isInteractionEnabled
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = stringResource(R.string.login_button),
                        color = Color.White,
                        fontFamily = IberPangeaFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.login_register_question),
                    fontFamily = IberPangeaFamily,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = stringResource(R.string.login_register_action),
                    fontFamily = IberPangeaFamily,
                    fontSize = 14.sp,
                    color = GreenDarkIberdrola,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            enabled = isInteractionEnabled
                        ) {
                            handleRegisterNavigation()
                        }
                        .padding(4.dp)
                )
            }
        }
    }
}
