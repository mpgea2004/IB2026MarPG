package com.iberdrola.practicas2026.MarPG.ui.factura_filter

/**
 * Eventos para la pantalla de filtros
 */
data class FilterEvents(
    //Actualiza las fechas
    val onDateFromChange: (String) -> Unit = {},
    val onDateToChange: (String) -> Unit = {},

    //Actualiza el rango de precios (min y max)
    val onPriceRangeChange: (Float, Float) -> Unit = { _, _ -> },

    //Añade o elimina un estado de la lista de seleccionados
    val onStatusToggle: (String) -> Unit = {},

    // Dispara la aplicación de filtros y la navegación atrás
    val onApply: () -> Unit = {},

    // Resetea todos los valores
    val onClear: () -> Unit = {}
)