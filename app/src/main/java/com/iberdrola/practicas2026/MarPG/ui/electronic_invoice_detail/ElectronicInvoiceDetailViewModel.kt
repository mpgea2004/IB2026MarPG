package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceViewModel @Inject constructor(
    private val updateUseCase: UpdateElectronicInvoiceUseCase
) : ViewModel() {

    var state by mutableStateOf(ElectronicInvoiceState())
        private set

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
        val isEmailValid = email.contains("@") && email.contains(".") && email.length > 5
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
}