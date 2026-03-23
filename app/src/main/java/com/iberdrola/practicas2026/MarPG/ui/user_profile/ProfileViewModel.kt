package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
        logAnalyticsUseCase("view_profile_mar")

        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { savedState ->
                if (state.name.isEmpty() && savedState.name.isNotEmpty()) {
                    state = savedState
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        state = state.copy(name = newName)
    }

    fun onEmailChange(newEmail: String) {
        state = state.copy(email = newEmail, emailError = null)
    }

    fun onPhoneChange(newPhone: String) {
        state = state.copy(phone = newPhone, phoneError = null)
    }

    fun onAddressChanged(newAddress: String) {
        state = state.copy(address = newAddress)
    }

    fun onPasswordChanged(newPassword: String) {
        state = state.copy(password = newPassword)
    }

    private fun isEmailValid(email: String): Boolean {
        return emailPattern.matches(email)
    }


    fun saveChanges(onSuccess: () -> Unit) {
        val isPhoneValid = state.phone.length == 9
        val isEmailValid = isEmailValid(state.email)

        if (!isEmailValid || !isPhoneValid) {
            // Log de error de validación
            logAnalyticsUseCase("profile_save_error", mapOf(
                "reason" to if (!isEmailValid) "invalid_email" else "invalid_phone"
            ))

            state = state.copy(
                emailError = if (!isEmailValid) "El formato del correo no es válido" else null,
                phoneError = if (!isPhoneValid) "El teléfono debe tener 9 dígitos" else null
            )
            return
        }

        viewModelScope.launch {
            userPrefs.updateProfile(state)
            logAnalyticsUseCase("profile_save_success")
            onSuccess()
        }
    }
}
