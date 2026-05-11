package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val logAnalytics: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(FilterState(isLoading = true))
        private set

    fun setInitialState(initialState: FilterState) {
        viewModelScope.launch {
            state = initialState.copy(isLoading = true)
            delay(600)
            state = initialState.copy(isLoading = false)
        }
    }
    fun onDateFromChange(date: String) {
        state = state.copy(dateFrom = date)
        logAnalytics("filter_select_date_from", mapOf("date" to date))
    }

    fun onDateToChange(date: String) {
        state = state.copy(dateTo = date)
        logAnalytics("filter_select_date_to", mapOf("date" to date))
    }

    fun onPriceRangeChange(min: Float, max: Float) {
        val exactMin = floor(min)
        val exactMax = floor(max)
        
        state = state.copy(minPrice = exactMin, maxPrice = exactMax)
        logAnalytics("filter_change_price_range", mapOf(
            "min_price" to exactMin.toDouble(),
            "max_price" to exactMax.toDouble(),
            "message" to "Rango de precio cambiado (entero)"
        ))
    }

    fun onDecimalRangeChange(minDec: Float, maxDec: Float) {
        val basePrice = floor(state.minPrice)
        val newMin = basePrice + minDec
        val newMax = basePrice + maxDec
        
        state = state.copy(minPrice = newMin, maxPrice = newMax)
        logAnalytics("filter_change_price_decimal", mapOf(
            "min_price" to newMin.toDouble(),
            "max_price" to newMax.toDouble(),
            "message" to "Rango de precio decimal cambiado"
        ))
    }

    fun onStatusToggle(status: String) {
        val current = state.selectedStatuses
        val updated = if (current.contains(status)) current - status else current + status
        state = state.copy(selectedStatuses = updated)
        logAnalytics("filter_toggle_status", mapOf(
            "status" to status,
            "message" to "Estado de factura cambiado"
        ))
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
        logAnalytics("filter_reset_click", mapOf(
            "action" to "limpiar_todo",
            "message" to "Todos los filtros han sido restablecidos"
        ))
    }
    init {
        logAnalytics("view_filter_screen", mapOf("message" to "Pantalla de filtros visualizada"))
    }

    fun onApply(){
        logAnalytics("filter_applied", mapOf(
            "min_price" to state.minPrice.toDouble(),
            "max_price" to state.maxPrice.toDouble(),
            "status_count" to state.selectedStatuses.size.toLong(),
            "message" to "Filtros aplicados correctamente"
        ))
    }
}