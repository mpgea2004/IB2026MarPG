package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionDashboardViewModel @Inject constructor(
    private val getInvoiceUseCase: GetInvoiceUseCase
) : ViewModel() {

    var state by mutableStateOf(ConsumptionState())
        private set

    init{
        loadData()
    }

    fun setCloudMode(isCloud: Boolean) {
        if (state.isCloud != isCloud) {
            state = state.copy(isCloud = isCloud)
            loadData()
        }
    }

    fun onTypeSelected(type: ContractType) {
        if (state.selectedType != type) {
            state = state.copy(selectedType = type)
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            
            try {
                val allInvoices = getInvoiceUseCase(state.isCloud).first()
                
                val filtered = allInvoices.filter { it.contractType == state.selectedType }
                    .sortedBy { DateMapper.toLocalDate(it.issueDate) }

                if (filtered.isEmpty()) {
                    state = state.copy(isLoading = false, chartData = emptyList(), comparisonMessage = "Sin datos disponibles")
                    return@launch
                }

                val chartPoints = filtered.takeLast(6).map { 
                    val date = DateMapper.toLocalDate(it.issueDate)
                    val monthLabel = "${date.month.name.take(3)} ${date.year.toString().takeLast(2)}"
                    monthLabel to it.amount
                }

                val lastInvoice = filtered.last()
                val message: String
                val positive: Boolean

                if (filtered.size >= 2) {
                    val previousInvoice = filtered[filtered.size - 2]
                    val diff = lastInvoice.amount - previousInvoice.amount
                    val percent = if (previousInvoice.amount != 0.0) {
                        (Math.abs(diff) / previousInvoice.amount) * 100
                    } else 0.0

                    positive = diff <= 0
                    val trend = if (diff <= 0) "menos" else "más"
                    message = "Has gastado un ${String.format("%.1f", percent)}% $trend que en tu factura anterior."
                } else {
                    message = "Esta es tu primera factura de ${if(state.selectedType == ContractType.LUZ) "Luz" else "Gas"}. ¡Bienvenido!"
                    positive = true
                }

                state = state.copy(
                    isLoading = false,
                    chartData = chartPoints,
                    comparisonMessage = message,
                    isPositiveTrend = positive
                )
            } catch (e: Exception) {
                state = state.copy(isLoading = false, comparisonMessage = "Error al cargar datos")
            }
        }
    }
}