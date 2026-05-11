package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var isGasEnabledConfig: Boolean? by mutableStateOf(null)
        private set

    init {
        logAnalyticsUseCase("view_seleccion_factura_electronica")
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            isFirstEmission = true
            if (!isRefreshing) state = ElectronicInvoiceListState.Loading
            errorMessage = null

            getElectronicInvoiceUseCase(isCloud = isCloud)
                .catch { e ->
                    delay(1500)
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
                    logAnalyticsUseCase(
                        "error_carga_factura_electronica",
                        mapOf("mensaje" to (e.message ?: "Error desconocido"))
                    )
                    if (localData.isEmpty()) {
                        state = ElectronicInvoiceListState.NoData
                    } else {
                        state = ElectronicInvoiceListState.Success(localData)
                    }
                    isRefreshing = false
                }
                .collect { invoiceList ->
                    val filteredList = if (isGasEnabledConfig == false) {
                        invoiceList.filter { it.type != ContractType.GAS }
                    } else {
                        invoiceList
                    }
                    localData = invoiceList

                    delay(1500)

                    if (isCloud && isFirstEmission) {
                        isFirstEmission = false
                    } else {
                        if (invoiceList.isEmpty()) {
                            state = ElectronicInvoiceListState.NoData
                        } else {
                            state = ElectronicInvoiceListState.Success(filteredList)
                            logAnalyticsUseCase(
                                "exito_carga_factura_electronica",
                                mapOf("cantidad" to filteredList.size)
                            )
                            errorMessage = null
                        }
                        isRefreshing = false
                    }
                }
        }
    }


    fun updateGasAvailability(show: Boolean) {
        if (isGasEnabledConfig != show) {
            isGasEnabledConfig = show
            loadInvoices()
        }
    }


    fun onElectronicInvoiceClick(invoice: ElectronicInvoice) {
        logAnalyticsUseCase(
            "click_seleccionar_factura_electronica", mapOf(
                "id_factura" to invoice.id,
                "tipo_contrato" to invoice.type.name
            )
        )
    }

    fun onBackClicked() {
        logAnalyticsUseCase("click_volver_seleccion_factura_electronica")
    }

    fun refreshInvoices() {
        viewModelScope.launch {
            isRefreshing = true
            logAnalyticsUseCase("click_reintentar_carga_factura_electronica")
            loadInvoices()
            delay(500)
            isRefreshing = false
        }
    }

    fun clearErrorMessage() {
        errorMessage = null
    }


    fun onNavigateStarted() {
        isNavigating = true
    }

    fun onNavigateFinished() {
        isNavigating = false
    }
}