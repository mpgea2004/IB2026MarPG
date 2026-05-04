package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceListViewModel @Inject constructor(
    private val getElectronicInvoiceUseCase: GetElectronicInvoiceUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<ElectronicInvoiceListState>(ElectronicInvoiceListState.Loading)
        private set

    var errorMessage by mutableStateOf<Int?>(null)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

    init {
        loadInvoices()
    }

    /**
     * Carga inicial y reintento
     */
    fun loadInvoices() {
        viewModelScope.launch {
            if (!isRefreshing) state = ElectronicInvoiceListState.Loading
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
                    
                    if (state !is ElectronicInvoiceListState.Success) {
                        state = ElectronicInvoiceListState.Error(errorRes)
                    }
                    isRefreshing = false
                }
                .collect { invoiceList ->
                    state = ElectronicInvoiceListState.Success(invoiceList)
                    isRefreshing = false
                }
        }
    }

    fun refreshInvoices() {
        viewModelScope.launch {
            isRefreshing = true
            loadInvoices()
            delay(500)
            isRefreshing = false
        }
    }

}