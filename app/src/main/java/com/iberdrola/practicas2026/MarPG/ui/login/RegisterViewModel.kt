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
class RegisterViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    fun onNameChanged(name: String) {
        state = state.copy(name = name, nameError = null)
    }

    fun onEmailChanged(email: String) {
        state = state.copy(email = email, emailError = null)
    }

    fun onPasswordChanged(password: String) {
        state = state.copy(password = password, passwordError = null)
    }
    fun onConfirmPasswordChanged(password: String) {
        state = state.copy(confirmPassword = password, confirmPasswordError = null)
    }

    fun onTogglePasswordVisibility() {
        state = state.copy(isPasswordVisible = !state.isPasswordVisible)
    }

    fun onToggleConfirmPasswordVisibility() {
        state = state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val isNameValid = state.name.isNotBlank()
        val isEmailValid = validateEmailUseCase(state.email)
        val isPasswordValid = state.password.length >= 6
        val passwordsMatch = state.password == state.confirmPassword

        val nameError = if (!isNameValid) "El nombre no puede estar vacío" else null
        val emailError = if (!isEmailValid) "Email no válido" else null
        val passwordError = if (!isPasswordValid) "Mínimo 6 caracteres" else null
        val confirmPasswordError = if (!passwordsMatch) "Las contraseñas no coinciden" else null

        state = state.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        val hasErrors = emailError != null || passwordError != null || confirmPasswordError != null || !isNameValid

        if (!hasErrors) {
            viewModelScope.launch {
                state = state.copy(isLoading = true)

                val currentProfile = userPrefs.userProfileFlow.first()
                val updatedProfile = currentProfile.copy(
                    name = state.name,
                    email = state.email,
                    password = state.password,
                    isLoggedIn = true
                )
                userPrefs.updateProfile(updatedProfile)

                logAnalytics("register_success", mapOf(
                    "email" to state.email,
                    "name" to state.name
                ), priority = AnalyticsPriority.HIGH)

                state = state.copy(isLoading = false, registerSuccess = true)
                onSuccess()
            }
        } else {
            logAnalytics("register_failure", mapOf(
                "name_error" to (nameError != null),
                "email_error" to (emailError != null),
                "password_error" to (passwordError != null),
                "match_error" to (confirmPasswordError != null)
            ), priority = AnalyticsPriority.MEDIUM)
        }
    }

    fun logAnalytics(name: String, params: Map<String, Any?> = emptyMap(), priority: AnalyticsPriority = AnalyticsPriority.MEDIUM) {
        logAnalyticsUseCase(name, params, priority)
    }
}
