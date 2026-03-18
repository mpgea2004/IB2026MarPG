package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice

/**
 * Representa las acciones que el usuario puede disparar desde la UI.
 * Al ser una data class, es fácil de pasar como un único objeto.
 */
data class ElectronicInvoiceListEvents(
    val onRetry: () -> Unit = {},
    val onElectronicInvoiceClick: (ElectronicInvoice) -> Unit = {},
)