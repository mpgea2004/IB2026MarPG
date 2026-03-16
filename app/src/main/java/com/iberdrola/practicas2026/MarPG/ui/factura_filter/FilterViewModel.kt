package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {

    // Estado del formulario de filtros
    var state by mutableStateOf(FilterState())
        private set

    /**
     * Inicializa el estado si queremos que al abrir la pantalla
     * ya aparezcan los filtros aplicados anteriormente.
     */
    fun setInitialState(initialState: FilterState) {
        state = initialState
    }

    // --- Funciones que alimentan los eventos ---

    fun onDateFromChange(date: String) {
        state = state.copy(dateFrom = date)
    }

    fun onDateToChange(date: String) {
        state = state.copy(dateTo = date)
    }

    fun onPriceRangeChange(min: Float, max: Float) {
        state = state.copy(minPrice = min, maxPrice = max)
    }

    fun onStatusToggle(status: String) {
        val current = state.selectedStatuses
        val updated = if (current.contains(status)) current - status else current + status
        state = state.copy(selectedStatuses = updated)
    }

    fun clearFilters() {
        state = FilterState() // Resetea a valores por defecto
    }
}