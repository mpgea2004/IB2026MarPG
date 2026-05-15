package com.iberdrola.practicas2026.MarPG.ui.factura_filter

data class FilterEvents(
    val onDateFromChange: (String) -> Unit = {},
    val onDateToChange: (String) -> Unit = {},
    val onPriceRangeChange: (Float, Float) -> Unit = { _, _ -> },
    val onDecimalRangeChange: (Float, Float) -> Unit = { _, _ -> },
    val onStatusToggle: (String) -> Unit = {},
    val onApply: () -> Unit = {},
    val onClear: () -> Unit = {}
)