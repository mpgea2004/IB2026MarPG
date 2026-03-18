package com.iberdrola.practicas2026.MarPG.ui.contrato_selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceListViewModel @Inject constructor(
    private val getElectronicInvoiceUseCase: GetElectronicInvoiceUseCase
) : ViewModel() {

    var state by mutableStateOf<ElectronicInvoiceListState>(ElectronicInvoiceListState.Loading)
        private set

    init {
        loadInvoices()
    }

    /**
     * Carga inicial y reintento
     */
    fun loadInvoices() {
        viewModelScope.launch {
            state = ElectronicInvoiceListState.Loading

            getElectronicInvoiceUseCase(isCloud = true)
                .catch { e ->
                    state = ElectronicInvoiceListState.Error(e.message ?: "Error desconocido")
                }
                .collect { invoiceList ->
                    state = ElectronicInvoiceListState.Success(invoiceList)
                }
        }
    }

    /**
     * Acción al pulsar en una factura
     */
    fun onElectronicInvoiceClick(invoice: ElectronicInvoice) {

    }
}