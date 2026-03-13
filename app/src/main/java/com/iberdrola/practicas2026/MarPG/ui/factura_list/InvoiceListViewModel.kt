package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


/** Lógica de carga, filtrado y agrupación de facturas por tipo y año */
@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val getInvoicesUseCase: GetInvoiceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // El estado que observa la UI
    var state by mutableStateOf<InvoiceListState>(InvoiceListState.LOADING)
        private set

    //Uso un State para avisar a la UI de cuando enseñar el feedback
    var shouldShowFeedback by mutableStateOf(false)
        private set

    //Es si se ha elegido la columna de luz o gas
    var selectedTab by mutableIntStateOf(0)
        private set

    //Caché de todas las facturas, así no tengo que consultar la base de datos cuando pulse en la pestaña de gas o luz
    var allInvoices : List<Invoice> = emptyList()

    //Obtengo el valor de la ruta, si no existe por defcto es false
    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

    //Estado para el mensaje de error
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadInvoices()
        observeFeedback() //empiezo a escuchar el feedback
    }

    /** Carga facturas desde el caso de uso y gestiona errores de flujo */
    private fun loadInvoices() {
        viewModelScope.launch {
            //Inicializo en LOADING para mostrar el esqueleto si no hay datos
            if (allInvoices.isEmpty()) {
                state = InvoiceListState.LOADING
            }
            errorMessage = null//Lo limpio

            getInvoicesUseCase(isCloud).catch { e ->
                //caturo la excepcion lanzada por el repo
                errorMessage = e.message

                //compruebo si la lista es vacia despues del error, y si es asi muestro el estado de no hay datos
                if(allInvoices.isEmpty()){
                    state = InvoiceListState.NODATA
                }else {
                    // Si ya teníamos la caché (del primer emit),
                    // simplemente notificamos el error pero mantenemos el estado SUCCESS
                    // La UI verá los datos y podrá mostrar un Toast con errorMessage
                    updateFilteredInvoices()
                }
            }.collect { invoices ->
                //recibo la lista(ya sea de red o de la caché de Room)
                allInvoices = invoices
                // Si la lista está vacía de verdad (ni caché ni red)
                if (allInvoices.isEmpty()) {
                    state = InvoiceListState.NODATA
                } else {
                    errorMessage = null // Si llega un collect con éxito, limpiamos errores previos
                    updateFilteredInvoices()
                }
            }
        }
    }
    /** Filtra por contrato y agrupa por año de emisión */
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

    /** Registra navegación de retorno para el conteo de feedback */
    fun registerBackNavigation() {
        viewModelScope.launch {
            // Solo notifico que el usuario salió de la pantalla
            checkFeedbackUseCase.notifyBackPress()
        }
    }
    /** Cambia de categoría y actualiza la lista */
    fun selectTab(index: Int) {
        selectedTab = index
        updateFilteredInvoices()
    }
    /** Resetea el mensaje de error */
    fun clearErrorMessage() {
        errorMessage = null
    }

    /** Función para refrescar las facturas */
    fun refreshInvoices() {
        loadInvoices()
    }

    // Para observar si mostramos el diálogo
    private fun observeFeedback() {
        viewModelScope.launch {
            // Uso la función del UseCase que mira si el contador llegó a 0
            checkFeedbackUseCase.shouldShowFeedback().collect { show ->
                // Si el Flow emite TRUE, activo la variable que levanta el BottomSheet en la UI
                shouldShowFeedback = show
            }
        }
    }
}