package com.iberdrola.practicas2026.MarPG.ui.factura_list

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice

sealed class InvoiceListState {
    data object LOADING : InvoiceListState()
    data object NODATA : InvoiceListState()
    data class SUCCESS(val groupedInvoices: Map<String, List<Invoice>>) : InvoiceListState() //en un principio tenia list, pero viendo que tengo que organizarlos en años, prefiero devolver un map con el año y la lista de las facturas de ese año
}