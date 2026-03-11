package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoiceUseCase
) : ViewModel() {
    // El estado que observa la UI
    var state by mutableStateOf<InvoiceListState>(InvoiceListState.LOADING)
        private set

    //Es si se ha elegido la columna de luz o gas
    var selectedTab by mutableIntStateOf(0)
        private set

    //Caché de todas las facturas, así no tengo que consultar la base de datos cuando pulse en la pestaña de gas o luz
    private var allInvoices : List<Invoice> = emptyList()

    init {
        loadInvoices()
    }

    private fun loadInvoices() {
        viewModelScope.launch {
            try {
                //Inicializo en LOADING por si acaso
                state = InvoiceListState.LOADING

                getInvoicesUseCase().collect { invoices ->
                    allInvoices = invoices
                    updateFilteredInvoices()
                }
            } catch (e: Exception) {
                //si el archivo no existe, el JSON está mal o falla la red...
                e.printStackTrace() // Para verlo en el Logcat
                state = InvoiceListState.NODATA
            }
        }
    }

    private fun updateFilteredInvoices() {
        //si no hay facturas tras la carga, estado ponemos el estado a NODATA directamente
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
            return
        }

        val type = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS

        //Filtro por tipo
        val filteredInvoices = allInvoices.filter { it.contractType == type }

        state = if (filteredInvoices.isEmpty()) {
            InvoiceListState.NODATA
        } else {
            //Agrupo por año de emisión
            val grouped = filteredInvoices.groupBy { invoice ->
                try {
                    DateMapper.toLocalDate(invoice.issueDate).year.toString()
                } catch (e: Exception) {
                    "Desconocido" //Por si alguna fecha viene con formato raro
                }
            }
            InvoiceListState.SUCCESS(grouped)
        }
    }

    fun selectTab(index: Int) {
        selectedTab = index
        updateFilteredInvoices()
    }
}