package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
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
        val isPhoneValid = state.phone.length == 9 || state.phone.isEmpty()

        if (!isEmailValid(state.email)) {
            state = state.copy(emailError = R.string.error_invalid_email_format)
            return
        }

        if (!isPhoneValid) {
            state = state.copy(phoneError = R.string.error_invalid_phone_format)
            return
        }
        viewModelScope.launch {
            userPrefs.updateProfile(state)
            onSuccess()
        }
    }
}
