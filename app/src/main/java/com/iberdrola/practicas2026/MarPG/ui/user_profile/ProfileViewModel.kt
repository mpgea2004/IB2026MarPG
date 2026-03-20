package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        state = state.copy(email = newEmail)
    }

    fun onPhoneChange(newPhone: String) {
        state = state.copy(phone = newPhone)
    }

    fun onAddressChanged(newAddress: String) {
        state = state.copy(address = newAddress)
    }

    fun onPasswordChanged(newPassword: String) {
        state = state.copy(password = newPassword)
    }

    fun saveChanges() {
        viewModelScope.launch {
            userPrefs.updateProfile(state)
        }
    }
}
