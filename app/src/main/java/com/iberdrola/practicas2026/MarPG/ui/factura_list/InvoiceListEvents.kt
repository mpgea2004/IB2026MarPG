package com.iberdrola.practicas2026.MarPG.ui.factura_list

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice

/** Eventos de usuario para la pantalla de listado de facturas */
data class InvoiceListEvents(
    val onDetail: (Invoice) -> Unit = {},
    val onFilter: () -> Unit = {},
    val onTabSelected: (Int) -> Unit = {},
    val onClearFilters: () -> Unit = {},
    val onSort: (SortOption) -> Unit = {},
    val onRemoveStatus: (String) -> Unit = {},
    val onRemoveDate: () -> Unit = {},
    val onRemovePrice: () -> Unit = {}
)