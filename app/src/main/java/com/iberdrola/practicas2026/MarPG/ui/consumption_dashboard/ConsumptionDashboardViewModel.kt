package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsumptionDashboardViewModel @Inject constructor(
    private val getInvoiceUseCase: GetInvoiceUseCase,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(ConsumptionState())
        private set

    private var loadJob: Job? = null

    init {
        logAnalyticsUseCase("view_dashboard_consumo")
        fetchRemoteConfig()
        loadData()
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        
        remoteConfig.setDefaultsAsync(mapOf("enseñar_contratos_gas" to true))
        
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val isGasEnabled = remoteConfig.getBoolean("enseñar_contratos_gas")
            state = state.copy(
                isGasEnabled = isGasEnabled,
                isConfigLoading = false
            )
            
            if (!isGasEnabled && state.selectedType == ContractType.GAS) {
                onTypeSelected(ContractType.LUZ)
            }
        }.addOnFailureListener {
            state = state.copy(isConfigLoading = false)
        }
    }

    fun setCloudMode(isCloud: Boolean) {
        if (state.isCloud != isCloud) {
            val modeName = if (isCloud) "Modo Cloud activado" else "Modo Local activado"
            logAnalyticsUseCase("click_toggle_cloud", mapOf("modo" to modeName))
            state = state.copy(
                isCloud = isCloud,
                isLoading = true
            )
            loadData()
        }
    }

    fun onTypeSelected(type: ContractType) {
        if (!state.isGasEnabled && type == ContractType.GAS) return

        if (state.selectedType != type) {
            logAnalyticsUseCase("click_seleccionar_tipo_contrato", mapOf("tipo" to type.name))
            state = state.copy(
                selectedType = type,
                chartData = emptyList(),
                comparisonMessage = UiText.DynamicString(""),
                isLoading = true
            )
            loadData()
        }
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val typeToLoad = state.selectedType
            val isCloudToLoad = state.isCloud

            if (!state.isLoading) {
                state = state.copy(isLoading = true)
            }

            getInvoiceUseCase(isCloudToLoad)
                .catch {
                    logAnalyticsUseCase("error_carga_consumo", mapOf("mensaje" to "Error al obtener facturas"))
                    state = state.copy(isLoading = false)
                }
                .collect { allInvoices ->
                    val filtered = allInvoices.filter { it.contractType == typeToLoad }
                        .sortedBy { DateMapper.toLocalDate(it.issueDate) }

                    if (filtered.isEmpty()) {
                        state = state.copy(
                            isLoading = false,
                            chartData = emptyList(),
                            comparisonMessage = UiText.StringResource(R.string.consumption_no_data),
                            isPositiveTrend = false
                        )
                    } else {
                        val chartPoints = filtered.takeLast(6).map {
                            val date = DateMapper.toLocalDate(it.issueDate)
                            val monthLabel = "${date.month.name.take(3)} ${date.year.toString().takeLast(2)}"
                            monthLabel to it.amount
                        }

                        val lastInvoice = filtered.last()
                        val message: UiText
                        val positive: Boolean

                        if (filtered.size >= 2) {
                            val previousInvoice = filtered[filtered.size - 2]
                            val diff = lastInvoice.amount - previousInvoice.amount
                            val percent = if (previousInvoice.amount != 0.0) {
                                (Math.abs(diff) / previousInvoice.amount) * 100
                            } else 0.0

                            positive = diff <= 0
                            val resId = if (diff <= 0) R.string.consumption_comparison_less else R.string.consumption_comparison_more
                            message = UiText.StringResource(resId, String.format("%.1f", percent))
                        } else {
                            val resId = if (typeToLoad == ContractType.LUZ) R.string.consumption_first_invoice_light else R.string.consumption_first_invoice_gas
                            message = UiText.StringResource(resId)
                            positive = true
                        }

                        state = state.copy(
                            isLoading = false,
                            chartData = chartPoints,
                            comparisonMessage = message,
                            isPositiveTrend = positive
                        )
                    }
                }
        }
    }
}