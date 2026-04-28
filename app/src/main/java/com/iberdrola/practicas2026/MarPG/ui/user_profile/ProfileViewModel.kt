package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileState(isLoading = true))
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
        loadSavedProfile()
    }

    private fun loadSavedProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(800)
            val savedState = userPrefs.userProfileFlow.first()
            state = savedState.copy(isSaved = true, isLoading = false)
        }
    }

    fun onNameChange(newName: String) {
        state = state.copy(name = newName, nameError = null, isSaved = false)
    }

    fun onEmailChange(newEmail: String) {
        state = state.copy(email = newEmail, emailError = null, isSaved = false)
    }

    fun onPhoneChange(newPhone: String) {
        state = state.copy(phone = newPhone, phoneError = null, isSaved = false)
    }

    fun onAddressChanged(newAddress: String) {
        state = state.copy(address = newAddress, isSaved = false)
    }

    fun onPasswordChanged(newPassword: String) {
        state = state.copy(password = newPassword, passwordError = null, isSaved = false)
    }

    private fun isEmailValid(email: String): Boolean {
        return emailPattern.matches(email)
    }

    fun saveChanges(onSuccess: () -> Unit) {
        state = state.copy(nameError = null, emailError = null, phoneError = null, passwordError = null)

        val isNameEmpty = state.name.trim().isEmpty()
        val isEmailInvalid = !isEmailValid(state.email)
        val isEmailEmpty = state.email.trim().isEmpty()
        val isPasswordEmpty = state.password.trim().isEmpty()
        val isPasswordTooShort = state.password.trim().isNotEmpty() && state.password.trim().length < 6
        val isPhoneInvalid = state.phone.isNotEmpty() && state.phone.length != 9

        var hasError = false

        if (isNameEmpty) {
            state = state.copy(nameError = R.string.error_field_required)
            hasError = true
        }
        if (isEmailEmpty) {
            state = state.copy(emailError = R.string.error_field_required)
            hasError = true
        } else {
            if (isEmailInvalid) {
                state = state.copy(emailError = R.string.error_invalid_email_format)
                hasError = true
            }
        }

        if (isPasswordEmpty) {
            state = state.copy(passwordError = R.string.error_field_required)
            hasError = true
        } else if (isPasswordTooShort) {
            state = state.copy(passwordError = R.string.error_password_too_short)
            hasError = true
        }

        if (isPhoneInvalid) {
            state = state.copy(phoneError = R.string.error_invalid_phone_format)
            hasError = true
        }

        if (!hasError) {
            viewModelScope.launch {
                state = state.copy(isSaving = true)
                delay(1500)
                userPrefs.updateProfile(state)
                state = state.copy(
                    isSaving = false,
                    isSaved = true,
                    saveJustFinished = true
                )
                onSuccess()
                delay(2500)
                state = state.copy(saveJustFinished = false)
            }
        }
    }

    fun onLogoutClick() {
        state = state.copy(showLogoutDialog = true)
    }

    fun onDismissLogoutDialog() {
        state = state.copy(showLogoutDialog = false)
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPrefs.clearProfile()
            state = ProfileState()
            onSuccess()
        }
    }
}