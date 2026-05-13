package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceListViewModel @Inject constructor(
    private val getElectronicInvoiceUseCase: GetElectronicInvoiceUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
) : ViewModel() {

    var state by mutableStateOf<ElectronicInvoiceListState>(ElectronicInvoiceListState.Loading)
        private set

    var errorMessage by mutableStateOf<Int?>(null)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var isNavigating by mutableStateOf(false)
        private set

    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false
    private var localData: List<ElectronicInvoice> = emptyList()
    private var isFirstEmission = true
    private var loadInvoicesJob: Job? = null

    var isGasEnabledConfig: Boolean? by mutableStateOf(null)
        private set

    init {
        logAnalyticsUseCase("view_seleccion_factura_electronica")
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        isGasEnabledConfig = null

        remoteConfig.setDefaultsAsync(mapOf("show_gas_contracts" to true))
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
        remoteConfig.setConfigSettingsAsync(configSettings)

        loadInvoices()

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    val newValue = remoteConfig.getBoolean("show_gas_contracts")
                    if (newValue != isGasEnabledConfig) {
                        isGasEnabledConfig = newValue
                        loadInvoices()
                    }
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        })

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isGasEnabledConfig = remoteConfig.getBoolean("show_gas_contracts")
            } else {
                isGasEnabledConfig = true
                if (isRefreshing) {
                    errorMessage = R.string.error_network_connection
                    isRefreshing = false
                }
            }
        }
    }

    fun loadInvoices() {
        loadInvoicesJob?.cancel()
        loadInvoicesJob = viewModelScope.launch {
            state = ElectronicInvoiceListState.Loading

            while (isGasEnabledConfig == null) {
                delay(50) }
            delay(2000)

            isFirstEmission = true
            errorMessage = null

            getElectronicInvoiceUseCase(isCloud = isCloud)
                .catch { e ->
                    val errorRes = when (e) {
                        is InvoiceException.NetworkError -> R.string.error_network_connection
                        is InvoiceException.NotFoundError -> R.string.error_data_not_found
                        is InvoiceException.ServerError -> {
                            if (e.code == 404) R.string.error_data_not_found
                            else R.string.error_server_maintenance
                        }
                        is InvoiceException.LocalDataError -> R.string.error_local_data
                        else -> R.string.error_unknown
                    }

                    errorMessage = errorRes
                    val filteredList = if (isGasEnabledConfig == false) {
                        localData.filter { it.type != ContractType.GAS }
                    } else {
                        localData
                    }
                    state = if (localData.isEmpty()) ElectronicInvoiceListState.NoData 
                            else ElectronicInvoiceListState.Success(filteredList)
                    isRefreshing = false
                }
                .collect { invoiceList ->
                    localData = invoiceList
                    val filteredList = if (isGasEnabledConfig == false) {
                        invoiceList.filter { it.type != ContractType.GAS }
                    } else {
                        invoiceList
                    }

                    if (isCloud && isFirstEmission) {
                        isFirstEmission = false
                    } else {
                        if (invoiceList.isEmpty() && !isFirstEmission) {
                            state = ElectronicInvoiceListState.NoData
                        } else if (invoiceList.isNotEmpty()) {
                            state = ElectronicInvoiceListState.Success(filteredList)
                            errorMessage = null
                        }

                        if (invoiceList.isNotEmpty() || !isFirstEmission || !isCloud) {
                            isRefreshing = false
                        }
                        isFirstEmission = false
                    }
                }
        }
    }

    fun refreshInvoices() {
        isRefreshing = true
        isGasEnabledConfig = null
        fetchRemoteConfig()
    }

    fun onElectronicInvoiceClick(invoice: ElectronicInvoice) {
        logAnalyticsUseCase("click_seleccionar_factura", mapOf("id" to invoice.id))
    }

    fun onBackClicked() { logAnalyticsUseCase("click_volver") }
    fun clearErrorMessage() { errorMessage = null }
    fun onNavigateStarted() { isNavigating = true }
    fun onNavigateFinished() { isNavigating = false }
}
