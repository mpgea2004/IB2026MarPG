package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ProfileValidationResult
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ValidateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val validateProfile: ValidateProfileUseCase
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
        logAnalyticsUseCase("view_profile_mar")
        observeProfile()
    }

    private fun observeProfile() {
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
        val validation = validateProfile(state.email, state.phone)

        if (validation is ProfileValidationResult.Error) {
            logAnalyticsUseCase("profile_save_error", mapOf(
                "reason" to if (validation.emailError != null) "invalid_email" else "invalid_phone"
            ))

            state = state.copy(
                emailError = validation.emailError,
                phoneError = validation.phoneError
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
