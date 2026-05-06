package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidatePhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ProfileValidationResult
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ValidateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val validateProfile: ValidateProfileUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
) : ViewModel() {

    var state by mutableStateOf(ProfileState(isLoading = true))
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
        loadSavedProfile()
        logAnalyticsUseCase("view_profile_mar")
    }

    private fun loadSavedProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(800)
            val savedState = userPrefs.userProfileFlow.first()
            state = savedState.copy(isSaved = true, isLoading = false, isEditMode = false, isSaveClicked = false, isVerifying = false)
        }
    }

    fun onEditClick() {
        if (state.password.isNotEmpty()) {
            state = state.copy(
                showSecurityDialog = true,
                securityPasswordInput = "",
                securityPasswordError = null
            )
        } else {
            state = state.copy(isEditMode = true)
        }
    }

    fun onSecurityPasswordChanged(input: String) {
        state = state.copy(securityPasswordInput = input, securityPasswordError = null)
    }

    fun onToggleSecurityPasswordVisibility() {
        state = state.copy(isSecurityPasswordVisible = !state.isSecurityPasswordVisible)
    }

    fun onSecurityConfirmClick() {
        if (state.securityPasswordInput == state.password) {
            viewModelScope.launch {
                state = state.copy(
                    showSecurityDialog = false,
                    isVerifying = true
                )
                delay(1500)
                state = state.copy(
                    isVerifying = false,
                    isEditMode = true,
                    securityPasswordInput = ""
                )
            }
        } else {
            state = state.copy(securityPasswordError = R.string.error_incorrect_password)
        }
    }

    fun onSecurityDismiss() {
        state = state.copy(showSecurityDialog = false, securityPasswordInput = "", securityPasswordError = null)
    }

    fun onDiscardClick() {
        loadSavedProfile()
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
            state = state.copy(isSaveClicked = true)
            viewModelScope.launch {
                state = state.copy(isSaving = true)
                delay(2000)
                userPrefs.updateProfile(state)
                state = state.copy(
                    isSaving = false,
                    isSaved = true,
                    saveJustFinished = true
                )
                onSuccess()
                delay(2000)
                state = state.copy(
                    saveJustFinished = false,
                    isEditMode = false,
                    isSaveClicked = false
                )
                logAnalyticsUseCase("profile_save_success")
            }
        }else{
            logAnalyticsUseCase("profile_save_error")
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
