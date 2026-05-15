package com.iberdrola.practicas2026.MarPG.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun onEmailChanged(email: String) {
        state = state.copy(email = email, emailError = null)
    }

    fun onPasswordChanged(password: String) {
        state = state.copy(password = password, passwordError = null)
    }

    fun onTogglePasswordVisibility() {
        state = state.copy(isPasswordVisible = !state.isPasswordVisible)
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val isEmailValid = validateEmailUseCase(state.email)
        val isPasswordValid = state.password.length >= 6

        val emailError = if (!isEmailValid) "Email no válido" else null
        val passwordError = if (!isPasswordValid) "La contraseña debe tener al menos 6 caracteres" else null

        state = state.copy(
            emailError = emailError,
            passwordError = passwordError
        )

        if (emailError == null && passwordError == null) {
            viewModelScope.launch {
                state = state.copy(isLoading = true)

                val currentProfile = userPrefs.userProfileFlow.first()
                val updatedProfile = currentProfile.copy(
                    email = state.email,
                    password = state.password,
                    isLoggedIn = true
                )
                userPrefs.updateProfile(updatedProfile)

                logAnalytics("login_success", mapOf("email" to state.email), priority = AnalyticsPriority.HIGH)

                state = state.copy(isLoading = false, loginSuccess = true)
                onSuccess()
            }
        } else {
            logAnalytics("login_failure", mapOf(
                "email_error" to (emailError != null),
                "password_error" to (passwordError != null)
            ), priority = AnalyticsPriority.MEDIUM)
        }
    }

    fun logAnalytics(name: String, params: Map<String, Any?> = emptyMap(), priority: AnalyticsPriority = AnalyticsPriority.MEDIUM) {
        logAnalyticsUseCase(name, params, priority)
    }
}
