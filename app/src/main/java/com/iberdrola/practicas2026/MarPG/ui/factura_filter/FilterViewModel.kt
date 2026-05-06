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
        val exactMin = min.toInt().toFloat()
        val exactMax = max.toInt().toFloat()
        state = state.copy(minPrice = exactMin, maxPrice = exactMax)
    }

    fun onStatusToggle(status: String) {
        val current = state.selectedStatuses
        val updated = if (current.contains(status)) current - status else current + status
        state = state.copy(selectedStatuses = updated)
        logAnalytics("filter_toggle_status", mapOf("status" to status))
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
        logAnalytics("filter_reset_click", mapOf("user_action" to "clear_all"))
    }
    init {
        logAnalytics("view_filter_screen")
    }

    fun onApply(){
        logAnalytics("filter_applied", mapOf(
            "min_price" to state.minPrice.toDouble(),
            "max_price" to state.maxPrice.toDouble(),
            "status_count" to state.selectedStatuses.size.toLong()
        ))
    }
}