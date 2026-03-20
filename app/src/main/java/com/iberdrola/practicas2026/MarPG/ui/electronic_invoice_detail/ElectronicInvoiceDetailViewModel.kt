package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceViewModel @Inject constructor(
    private val updateUseCase: UpdateElectronicInvoiceUseCase,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(ElectronicInvoiceState())
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    init {
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                state = state.copy(
                    userProfile = profile
                )

                if (state.emailInput.isEmpty() && profile.email.isNotEmpty()) {
                    state = state.copy(emailInput = profile.email)
                }
            }
        }
    }

    fun selectContract(contract: ElectronicInvoice) {
        state = state.copy(
            selectedContract = contract,
            emailInput = contract.email ?: "",
            isEditingEmail = contract.isEnabled,
            isLegalAccepted = false,
            isSuccess = false,
            error = null
        )
    }

    fun onEmailChanged(nuevoEmail: String) {
        state = state.copy(emailInput = nuevoEmail)
    }

    fun onLegalAccepted(accepted: Boolean) {
        state = state.copy(isLegalAccepted = accepted)
    }

    /**
     * Validación del botón de continuar.
     */
    fun canContinue(): Boolean {
        val email = state.emailInput.trim()
        val isEmailValid = emailPattern.matches(email)
        val contract = state.selectedContract

        if (contract == null) {
            return false
        }

        val result = if (contract.isEnabled == false) {
            isEmailValid && state.isLegalAccepted
        } else {
            isEmailValid
        }

        return result
    }

    fun performUpdate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val updatedContract = contract.copy(
                    isEnabled = true,
                    email = state.emailInput
                )

                updateUseCase(updatedContract)

                state = state.copy(
                    selectedContract = updatedContract,
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun performDeactivate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true, isSuccess = false)

            try {
                val updatedContract = contract.copy(
                    isEnabled = false
                )

                updateUseCase(updatedContract)

                state = state.copy(
                    selectedContract = updatedContract,
                    isDeactivation = true,
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onOtpChanged(nuevoOtp: String) {
        if (nuevoOtp.length <= 6) state = state.copy(otpInput = nuevoOtp)
    }

    fun onResendOtp() {
        if (state.resendAttempts > 0) {
            viewModelScope.launch {
                state = state.copy(
                    resendAttempts = state.resendAttempts - 1,
                    isLoading = true,
                    showResendSuccess = false
                )
                delay(1500)
                state = state.copy(isLoading = false, showResendSuccess = true)
            }
        }
    }

    fun closeResendBanner() {
        state = state.copy(showResendSuccess = false)
    }

    fun onShowLegalDetail(title: String, content: String) {
        state = state.copy(
            selectedLegalTitle = title,
            selectedLegalContent = content,
            showLegalSheet = true
        )
    }

    fun onDismissLegalSheet() {
        state = state.copy(showLegalSheet = false)
    }

    fun onContinueClick(onNavigateToOtp: () -> Unit) {
        if (state.userProfile.phone.isEmpty()) {
            state = state.copy(showNoPhoneDialog = true)
        } else {
            onNavigateToOtp()
        }
    }

    fun onNewPhoneChanged(nuevoTelefono: String) {
        if (nuevoTelefono.all { it.isDigit() } && nuevoTelefono.length <= 9) {
            state = state.copy(newPhoneInput = nuevoTelefono)
        }
    }

    fun onPasswordChanged(nuevaPass: String) {
        state = state.copy(passwordInput = nuevaPass)
    }

    fun savePhoneAndContinue(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                delay(1000)

                val userSavedPassword = state.userProfile.password

                val isPasswordCorrect = if (userSavedPassword.isEmpty()) {
                    state.passwordInput == "1234"
                } else {
                    state.passwordInput == userSavedPassword
                }

                if (isPasswordCorrect) {
                    userPrefs.updatePhone(state.newPhoneInput)

                    state = state.copy(
                        showNoPhoneDialog = false,
                        passwordInput = "",
                        error = null
                    )
                    onSuccess()
                } else {
                    state = state.copy(
                        error = "La contraseña introducida no es correcta"
                    )
                }
            } catch (e: Exception) {
                state = state.copy(error = "Ha ocurrido un error inesperado")
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun closePhoneDialog() {
        state = state.copy(showNoPhoneDialog = false, error = null, passwordInput = "")
    }
}