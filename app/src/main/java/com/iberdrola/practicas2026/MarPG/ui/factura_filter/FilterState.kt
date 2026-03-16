package com.iberdrola.practicas2026.MarPG.ui.factura_filter

data class FilterState(
    val dateFrom: String = "",
    val dateTo: String = "",
    val maxPrice: Float = 300f,
    val selectedStatuses: Set<String> = emptySet()
)