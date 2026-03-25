package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Pantalla principal de filtrado de facturas
 * Se encarga de coordinar la sincronización entre el ViewModel de la lista y el de filtros,
 * además de gestionar la navegación de retorno
 */
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val logAnalytics: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(FilterState())
        private set

    val events = FilterEvents(
        onDateFromChange = { date ->
            state = state.copy(dateFrom = date)
            logAnalytics("filter_select_date_from", mapOf("date" to date))
        },
        onDateToChange = { date ->
            state = state.copy(dateTo = date)
            logAnalytics("filter_select_date_to", mapOf("date" to date))
        },
        onPriceRangeChange = { min, max ->
            val exactMin = min.toInt().toFloat()
            val exactMax = max.toInt().toFloat()
            state = state.copy(minPrice = exactMin, maxPrice = exactMax)
        },
        onStatusToggle = { status ->
            val current = state.selectedStatuses
            val updated = if (current.contains(status)) current - status else current + status
            state = state.copy(selectedStatuses = updated)
            logAnalytics("filter_toggle_status", mapOf("status" to status))
        },
        onClear = {
            logAnalytics("filter_reset_click", mapOf("user_action" to "clear_all"))
            state = FilterState()
        },
        onApply = {
            logAnalytics("filter_applied", mapOf(
                "min_price" to state.minPrice.toDouble(),
                "max_price" to state.maxPrice.toDouble(),
                "status_count" to state.selectedStatuses.size.toLong()
            ))
        }
    )

    init {
        logAnalytics("view_filter_screen")
    }

    fun onClearWithLimits(min: Float, max: Float) {
        state = FilterState(minPrice = min, maxPrice = max)
    }
    fun setInitialState(initialState: FilterState) {
        state = initialState
    }
}