package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil


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

    //Estado para el filtro
    var currentFilterState by mutableStateOf(FilterState())
        private set

    /** Indica si se está realizando una operación de refresco manual (Pull-to-refresh) */
    var isRefreshing by mutableStateOf(false)
        private set

    /** Límite inferior real basado en la factura más barata */
    val minInvoiceAmount: Float
        get() = allInvoices.minOfOrNull { it.amount.toFloat() } ?: 0f

    /** Límite superior real basado en la factura más cara
     * Aplico ceil() para que si la factura es 185.5, el límite sea 186
     * Esto garantiza que las facturas con decimales no queden excluidas por el redondeo del slider
     */
    val maxInvoiceAmount: Float
        get() = allInvoices.maxOfOrNull { it.amount.toFloat() }?.let { ceil(it) } ?: 500f

    init {
        loadInvoices()
        observeFeedback() //empiezo a escuchar el feedback
    }

    /** Carga facturas desde el caso de uso y gestiona errores de flujo */
    private fun loadInvoices() {
        viewModelScope.launch {
            prepareLoadingState()

            getInvoicesUseCase(isCloud)
                .catch { handleLoadError(it) }
                .collect { invoices ->
                    allInvoices = invoices

                    // Extraemos la lógica del filtro a su propia función
                    setupInitialFilterPrices(invoices)

                    if (invoices.isNotEmpty()) {
                        errorMessage = null
                    }

                    updateFilteredInvoices()
                }
        }
    }

    /** Configura el estado inicial antes de la carga */
    private fun prepareLoadingState() {
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.LOADING
        }
        errorMessage = null
    }

    /** Gestiona los errores ocurridos durante el flujo de datos */
    private fun handleLoadError(e: Throwable) {
        errorMessage = if (e is InvoiceException) e.message else InvoiceException.Unknown.message
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
        }
    }

    /** * Ajusta los límites del filtro solo la primera vez que recibimos datos reales
     */
    private fun setupInitialFilterPrices(invoices: List<Invoice>) {
        val isFilterDefault = currentFilterState.minPrice == 0f && currentFilterState.maxPrice == 500f

        if (invoices.isNotEmpty() && isFilterDefault) {
            currentFilterState = currentFilterState.copy(
                minPrice = minInvoiceAmount,
                maxPrice = maxInvoiceAmount
            )
        }
    }
    /** Filtra por contrato, importe, estado, fecha y agrupa por año de emisión
     * No he hecho otro dao filtrado, porque para una aplicación como la que estoy haciendo(de maximo 100 facturas, ya que es de prueba) no vale la pena estar haciendo peticiones a la bd, mejor hago una y esa la filtro
     */
    private fun updateFilteredInvoices() {
        //Si no hay facturas cargadas, mueetro NODATA directamente
        if (allInvoices.isEmpty() && state is InvoiceListState.LOADING) {
            return
        }

        //Si la base de datos está realmente vacía (sin filtros), entonces sí es NODATA total
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
            return
        }

        //Escojo el tipo de contrato según la pestaña activa
        val contractTypeFilter = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS

        //Aplico todos los filtros del currentFilterState
        val filteredInvoices = allInvoices.filter { invoice ->
            //Filtro por Pestaña(Luz/Gas)
            val matchesType = invoice.contractType == contractTypeFilter

            //Filtro por Importe(Slider)
            val matchesPrice = invoice.amount >= currentFilterState.minPrice &&
                    invoice.amount <= currentFilterState.maxPrice

            //Filtro por Estado(Checkboxes)
            //Si no hay ninguno seleccionado, permitimos todos(true)
            val matchesStatus = currentFilterState.selectedStatuses.isEmpty() ||
                    currentFilterState.selectedStatuses.contains(invoice.status.description)

            //Filtro por Fecha (Calendarios)
            val matchesDate = checkDateRange(invoice.issueDate)

            //La factura debe cumplir todas las condiciones
            matchesType && matchesPrice && matchesStatus && matchesDate
        }

        // Actualizo el estado de la UI según el resultado, si no hay facturas, vacio
        val groupedByYear = filteredInvoices.groupBy { invoice ->
            try {
                DateMapper.toLocalDate(invoice.issueDate).year.toString()
            } catch (e: Exception) {
                "Sin fecha"
            }
        }

        state = InvoiceListState.SUCCESS(groupedByYear)

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
        viewModelScope.launch {
            isRefreshing = true
            loadInvoices()
            delay(500)
            isRefreshing = false
        }
    }

    /** Observa el flujo de feedback para determinar cuándo se debe mostrar el diálogo de valoración al usuario */
    private fun observeFeedback() {
        viewModelScope.launch {
            // Uso la función del UseCase que mira si el contador llegó a 0
            checkFeedbackUseCase.shouldShowFeedback().collect { show ->
                // Si el Flow emite TRUE, activo la variable que levanta el BottomSheet en la UI
                shouldShowFeedback = show
            }
        }
    }

    //-----------Tarea 2

    /**
     * Se llama desde la FilterScreen al pulsar "Aplicar", y aplica los filtros
     * Recibe el estado final del FilterViewModel
     */
    fun applyFilters(newFilters: FilterState) {
        currentFilterState = newFilters
        updateFilteredInvoices()
    }

    /**
     * Comprueba si la fecha de la factura entra en el rango seleccionado
     */
    private fun checkDateRange(invoiceDateStr: String): Boolean {
        //Si no hay fechas seleccionadas, pasa el filtro
        if (currentFilterState.dateFrom.isEmpty() && currentFilterState.dateTo.isEmpty()) {
            return true
        }

        return try {
            //Convierto la fecha de la factura
            val invoiceDate = DateMapper.toLocalDate(invoiceDateStr)

            //Comprubo el límite inferior(Desde)
            val matchesFrom = if (currentFilterState.dateFrom.isNotEmpty()) {
                val fromDate = DateMapper.toLocalDate(currentFilterState.dateFrom)
                !invoiceDate.isBefore(fromDate)
            } else true

            //Compruebo el límite superior (Hasta)
            val matchesTo = if (currentFilterState.dateTo.isNotEmpty()) {
                val toDate = DateMapper.toLocalDate(currentFilterState.dateTo)
                !invoiceDate.isAfter(toDate)
            } else true

            matchesFrom && matchesTo
        } catch (e: Exception) {
            //Si el usuario escribió una fecha incompleta o mal formateada,
            //no descarto la factura para no dar sensación de lista vacía por error.
            true
        }
    }
}