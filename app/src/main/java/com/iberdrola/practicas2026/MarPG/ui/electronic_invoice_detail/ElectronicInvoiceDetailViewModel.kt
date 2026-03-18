package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceViewModel @Inject constructor(
    private val updateUseCase: UpdateElectronicInvoiceUseCase
) : ViewModel() {

    var state by mutableStateOf(ElectronicInvoiceState())
        private set

    val events = ElectronicInvoiceEvents(
        onSelectContract = { contract ->
            state = state.copy(
                selectedContract = contract,
                emailInput = contract.email ?: ""
            )
        },
        onEmailChange = { newEmail ->
            state = state.copy(emailInput = newEmail)
        },
        onLegalCheckChange = { accepted ->
            state = state.copy(isLegalAccepted = accepted)
        },
        onConfirmUpdate = {
            performUpdate()
        },
        onExitFlow = {
            state = ElectronicInvoiceState() // Resetear todo al terminar
        }
    )

    private fun performUpdate() {
        val contract = state.selectedContract ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // Llamada al caso de uso (Repository -> Room/API)
                updateUseCase(contract, state.emailInput)
                state = state.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Validación del botón según el estado del contrato
    fun canContinue(): Boolean {
        val isEmailValid = state.emailInput.contains("@") && state.emailInput.contains(".")
        val isEnabled = state.selectedContract?.isEnabled ?: false

        return if (isEnabled) {
            isEmailValid // Flujo Modificar
        } else {
            isEmailValid && state.isLegalAccepted // Flujo Activar
        }
    }
}