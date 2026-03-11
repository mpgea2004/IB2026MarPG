package com.iberdrola.practicas2026.MarPG.ui.factura_list

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice

data class InvoiceListEvents(
    val onDetail: (Invoice) -> Unit = {},
    val onFilter: () -> Unit = {},
    val onTabSelected: (Int) -> Unit = {} //0 para luz, 1 para gas
)