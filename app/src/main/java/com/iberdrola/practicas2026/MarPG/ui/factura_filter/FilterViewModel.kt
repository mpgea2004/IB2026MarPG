package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {

    // Estado del formulario de filtros
    var state by mutableStateOf(FilterState())
        private set

    /**
     * Sincroniza el estado del filtro con los valores que ya estaban aplicados
     * en la pantalla de la lista.
     */
    fun setInitialState(initialState: FilterState) {
        state = initialState
    }
    // --- Métodos de actualización de estado (Events) ---
    fun onDateFromChange(date: String) {
        state = state.copy(dateFrom = date)
    }

    fun onDateToChange(date: String) {
        state = state.copy(dateTo = date)
    }
    /**
     * Actualiza el rango de precios. Al usar .copy() aseguramos la inmutabilidad
     * y notificamos a la UI del cambio.
     */
    fun onPriceRangeChange(min: Float, max: Float) {
        //Uso ceil para que el rango siempre cubra los decimales hacia arriba
        val roundedMin = ceil(min)
        val roundedMax = ceil(max)

        state = state.copy(minPrice = roundedMin, maxPrice = roundedMax)
    }

    /**
     * Gestiona la selección/deselección múltiple de estados.
     * Si el estado ya existe en el Set, lo elimina; si no, lo añade.
     */
    fun onStatusToggle(status: String) {
        val current = state.selectedStatuses
        val updated = if (current.contains(status)) current - status else current + status
        state = state.copy(selectedStatuses = updated)
    }
    /**
     * Resetea todos los campos a sus valores iniciales.
     * Los límites de precio son dinámicos para ajustarse a las facturas reales.
     */
    fun clearFilters(minLimit: Float, maxLimit: Float) {
        state = FilterState(
            minPrice = minLimit,
            maxPrice = maxLimit,
            dateFrom = "",
            dateTo = "",
            selectedStatuses = emptySet()
        )
    }
}