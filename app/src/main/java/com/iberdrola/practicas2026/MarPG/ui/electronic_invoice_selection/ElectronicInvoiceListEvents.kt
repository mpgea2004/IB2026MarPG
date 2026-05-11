package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
data class ElectronicInvoiceListEvents(
    val onRetry: () -> Unit = {},
    val onElectronicInvoiceClick: (ElectronicInvoice) -> Unit = {},
)