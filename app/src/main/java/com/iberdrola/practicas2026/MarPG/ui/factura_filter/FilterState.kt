package com.iberdrola.practicas2026.MarPG.ui.factura_filter

/**
 * Estado que representa los filtros seleccionados por el usuario.
 */
data class FilterState(
    val dateFrom: String = "",
    val dateTo: String = "",
    val minPrice: Float = 0f,   //Valor mínimo inicial
    val maxPrice: Float = 500f, //Valor máximo inicial
    val selectedStatuses: Set<String> = emptySet(),
    val isLoading: Boolean = false
)