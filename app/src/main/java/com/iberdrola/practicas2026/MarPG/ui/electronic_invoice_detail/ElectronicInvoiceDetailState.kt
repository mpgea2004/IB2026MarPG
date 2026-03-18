package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice

data class ElectronicInvoiceState(
    // Datos del contrato
    val selectedContract: ElectronicInvoice? = null,
    val emailInput: String = "",
    val isLegalAccepted: Boolean = false,

    // Control de UI
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false, // Para navegar al "OK"
    val error: String? = null,

    // Paso actual (opcional, por si quieres controlar la lógica de pantallas aquí)
    val currentStep: ElectronicInvoiceStep = ElectronicInvoiceStep.SELECTION
)

enum class ElectronicInvoiceStep {
    SELECTION, FORM, VERIFICATION, SUCCESS
}