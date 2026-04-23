package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Pantalla principal de filtrado de facturas
 */
@HiltViewModel
class FilterViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(FilterState(isLoading = true))
        private set

    /**
     * Sincroniza el estado del filtro con los valores que ya estaban aplicados
     * en la pantalla de la lista.
     */
    fun setInitialState(initialState: FilterState) {
        viewModelScope.launch {
            state = initialState.copy(isLoading = true)
            // Simulación de carga para mostrar el shimmer
            delay(600)
            state = initialState.copy(isLoading = false)
        }
    }
    // --- Métodos de actualización de estado (Events) ---
    fun onDateFromChange(date: String) {
        state = state.copy(dateFrom = date)
    }

    fun onDateToChange(date: String) {
        state = state.copy(dateTo = date)
    }

    fun onPriceRangeChange(min: Float, max: Float) {
        val exactMin = min.toInt().toFloat()
        val exactMax = max.toInt().toFloat()
        state = state.copy(minPrice = exactMin, maxPrice = exactMax)
    }

    fun onStatusToggle(status: String) {
        val current = state.selectedStatuses
        val updated = if (current.contains(status)) current - status else current + status
        state = state.copy(selectedStatuses = updated)
    }

    fun clearFilters(minLimit: Float, maxLimit: Float) {
        state = FilterState(
            minPrice = minLimit,
            maxPrice = maxLimit,
            dateFrom = "",
            dateTo = "",
            selectedStatuses = emptySet(),
            isLoading = false
        )
    }
}