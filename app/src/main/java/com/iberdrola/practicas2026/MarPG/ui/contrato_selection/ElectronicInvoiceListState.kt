package com.iberdrola.practicas2026.MarPG.ui.contrato_selection

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice

sealed class ElectronicInvoiceListState {
    object Loading : ElectronicInvoiceListState()
    data class Success(val contracts: List<ElectronicInvoice>) : ElectronicInvoiceListState()
    data class Error(val message: String) : ElectronicInvoiceListState()
}