package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

    init {
        logAnalyticsUseCase("view_electronic_invoice_selection")
        loadInvoices()
    }

    /**
     * Carga inicial y reintento
     */
    fun loadInvoices() {
        viewModelScope.launch {
            state = ElectronicInvoiceListState.Loading

            getElectronicInvoiceUseCase(isCloud = isCloud)
                .catch { e ->
                    state = ElectronicInvoiceListState.Error(e.message ?: "Error desconocido")
                    logAnalyticsUseCase("elec_invoice_load_error", mapOf("error" to (e.message ?: "unknown")))
                }
                .collect { invoiceList ->
                    state = ElectronicInvoiceListState.Success(invoiceList)
                    logAnalyticsUseCase("elec_invoice_load_success", mapOf("count" to invoiceList.size))
                }
        }
    }


    fun onElectronicInvoiceClick(invoice: ElectronicInvoice) {
        logAnalyticsUseCase("elec_invoice_selected", mapOf(
            "id" to invoice.id,
            "type" to invoice.type.name
        ))
    }

    fun onBackClicked() {
        logAnalyticsUseCase("elec_invoice_back_click")
    }

    fun onRetryClicked() {
        logAnalyticsUseCase("elec_invoice_retry_click")
        loadInvoices()
    }
}