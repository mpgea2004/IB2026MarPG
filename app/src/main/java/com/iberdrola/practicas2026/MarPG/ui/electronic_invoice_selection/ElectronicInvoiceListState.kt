package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice

sealed class ElectronicInvoiceListState {
    data object Loading : ElectronicInvoiceListState()
    data object NoData : ElectronicInvoiceListState()
    data class Success(val contracts: List<ElectronicInvoice>) : ElectronicInvoiceListState()
}