package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    // --- Funciones de actualización de State ---

    fun onEmailChanged(nuevoEmail: String) {
        state = state.copy(emailInput = nuevoEmail)
    }

    fun onOtpChanged(nuevoOtp: String) {
        // Limitamos a 6 caracteres para el código SMS
        if (nuevoOtp.length <= 6) {
            state = state.copy(otpInput = nuevoOtp)
        }
    }

    fun onLegalAccepted(accepted: Boolean) {
        state = state.copy(isLegalAccepted = accepted)
    }

    fun closeResendBanner() {
        state = state.copy(showResendSuccess = false)
    }

    // --- Lógica de Negocio ---

     fun performUpdate() {
        val contract = state.selectedContract ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // Llamada al caso de uso
                updateUseCase(contract, state.emailInput)
                state = state.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
            }
        }
    }

    // --- Validaciones ---
    // Validación del botón según el estado del contrato
    fun canContinue(): Boolean {
        val isEmailValid = state.emailInput.contains("@") && state.emailInput.contains(".")
        val isEnabled = state.selectedContract?.isEnabled ?: false

            return if (!isEnabled) {
                isEmailValid && state.isLegalAccepted
            } else {
                isEmailValid
            }
    }
    fun onResendOtp() {
        viewModelScope.launch {
            //Pongo cargando y oculto el banner si ya estaba
            state = state.copy(isLoading = true, showResendSuccess = false)

            //Simul la espera de red
            delay(1500)

            //Quito carga y muestro el éxito
            state = state.copy(isLoading = false, showResendSuccess = true)
        }
    }
}