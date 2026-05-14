package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
        logAnalyticsUseCase("view_dashboard_consumo", priority = AnalyticsPriority.HIGH)
        fetchRemoteConfig()
        loadData()
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
            fetchTimeoutInSeconds = 2
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf("show_gas_contracts" to true)
        remoteConfig.setDefaultsAsync(defaults).addOnCompleteListener {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                val isEnabled = if (task.isSuccessful) {
                    remoteConfig.getBoolean("show_gas_contracts")
                } else {
                    true
                }
                updateGasAvailability(isEnabled)
            }
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    updateGasAvailability(remoteConfig.getBoolean("show_gas_contracts"))
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        })
    }

    private fun updateGasAvailability(isEnabled: Boolean) {
        state = state.copy(
            isGasEnabled = isEnabled,
            isConfigLoading = false
        )

        logAnalyticsUseCase("actualizacion_remota_gas_dashboard", mapOf("habilitado" to isEnabled), priority = AnalyticsPriority.LOW)

        if (!isEnabled && state.selectedType == ContractType.GAS) {
            onTypeSelected(ContractType.LUZ)
        }
    }

    fun setCloudMode(isCloud: Boolean) {
        if (state.isCloud != isCloud) {
            val modeName = if (isCloud) "Cloud" else "Local"
            logAnalyticsUseCase("click_cambiar_modo_datos", mapOf("modo" to modeName), priority = AnalyticsPriority.MEDIUM)
            state = state.copy(
                isCloud = isCloud,
                isLoading = true
            )
            loadData()
        }
    }

    fun onTypeSelected(type: ContractType) {
        if (state.isGasEnabled == false && type == ContractType.GAS) return

        if (state.selectedType != type) {
            logAnalyticsUseCase("click_cambio_tipo_grafico", mapOf("tipo" to type.name), priority = AnalyticsPriority.MEDIUM)
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

            if (!isCloudToLoad) {
                delay((1000..3000).random().toLong())
            } else {
                delay(300)
            }

            getInvoiceUseCase(isCloudToLoad)
                .catch {
                    logAnalyticsUseCase("error_carga_dashboard", mapOf("tipo" to typeToLoad.name), priority = AnalyticsPriority.HIGH)
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
