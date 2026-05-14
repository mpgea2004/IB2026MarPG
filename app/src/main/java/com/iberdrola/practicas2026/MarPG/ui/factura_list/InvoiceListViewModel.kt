package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

enum class SortOption {
    DATE,
    PRICE,
    TYPE
}

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val getInvoicesUseCase: GetInvoiceUseCase,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val userPrefs: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

    var state by mutableStateOf<InvoiceListState>(InvoiceListState.LOADING)
        private set

    var isGasEnabled: Boolean? by mutableStateOf(null)
        private set

    var userAddress by mutableStateOf("")
        private set

    var shouldShowFeedback by mutableStateOf(false)
        private set

    var selectedTab by mutableIntStateOf(0)
        private set

    var allInvoices : List<Invoice> = emptyList()

    var errorMessage by mutableStateOf<Int?>(null)
        private set

    var currentFilterState by mutableStateOf(FilterState())
        private set

    var currentSortOption by mutableStateOf(SortOption.DATE)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var selectedInvoice by mutableStateOf<Invoice?>(null)
        private set

    var showSingleInvoiceDialog by mutableStateOf(false)
        private set

    var showRefreshDialog by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isAmountVisible by mutableStateOf(true)
        private set

    var shouldScrollToHistoric by mutableStateOf(false)
        private set

    var shouldScrollToTop by mutableStateOf(false)
        private set

    private var lastMinLimit: Float = 0f
    private var lastMaxLimit: Float = 500f

    val minInvoiceAmount: Float
        get() = allInvoices.minOfOrNull { it.amount.toFloat() }?.let { floor(it) } ?: 0f

    val maxInvoiceAmount: Float
        get() = allInvoices.maxOfOrNull { it.amount.toFloat() }?.let { ceil(it) } ?: 500f

    val minInvoiceDate: String?
        get() = allInvoices.minByOrNull { DateMapper.toLocalDate(it.issueDate) }?.issueDate

    val maxInvoiceDate: String?
        get() = allInvoices.maxByOrNull { DateMapper.toLocalDate(it.issueDate) }?.issueDate

    init {
        logAnalyticsUseCase("view_lista_facturas", priority = AnalyticsPriority.HIGH)
        observeFeedback()
        observeUserProfile()
        observeAmountVisibility()
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig(onComplete: () -> Unit = {}) {
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
                onComplete()
            }
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    updateGasAvailability(remoteConfig.getBoolean("show_gas_contracts"))
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                FirebaseCrashlytics.getInstance().recordException(error)
            }
        })
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                userAddress = profile.address
            }
        }
    }


    fun onFilterClicked() {
        logAnalyticsUseCase("click_filtrar_facturas", mapOf("origen" to "boton_cabecera"), priority = AnalyticsPriority.MEDIUM)
    }

    fun onInvoiceClick(invoice: Invoice) {
        logAnalyticsUseCase("click_detalle_factura", mapOf(
            "id_factura" to invoice.id,
            "tipo_contrato" to invoice.contractType.name,
            "importe" to invoice.amount.toString()
        ), priority = AnalyticsPriority.MEDIUM)
    }
    private fun observeAmountVisibility() {
        viewModelScope.launch {
            userPrefs.amountVisibleFlow.collect { visible ->
                isAmountVisible = visible
            }
        }
    }

    private fun loadInvoices(resetFilters: Boolean = false) {
        viewModelScope.launch {
            if (resetFilters) {
                currentFilterState = FilterState()
                searchQuery = ""
                lastMinLimit = 0f
                lastMaxLimit = 500f
            }

            prepareLoadingState()

            if (!isCloud) {
                delay((1000..3000).random().toLong())
            } else {
                delay(300)
            }

            getInvoicesUseCase(isCloud)
                .catch { e->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    handleLoadError(e)
                    logAnalyticsUseCase("error_carga_facturas", mapOf("mensaje" to (e.message ?: "error desconocido")), priority = AnalyticsPriority.HIGH)
                }
                .collect { invoices ->
                    allInvoices = invoices
                    setupDynamicFilterPrices(invoices)
                    sanitizeFilterDates(invoices)

                    if (invoices.isEmpty()) {
                        state = InvoiceListState.NODATA
                    } else {
                        errorMessage = null
                        setupDynamicFilterPrices(invoices)
                        sanitizeFilterDates(invoices)
                        updateFilteredInvoices()
                    }
                }
        }
    }

    private fun prepareLoadingState() {
        allInvoices = emptyList()
        state = InvoiceListState.LOADING
        errorMessage = null
    }

    private fun handleLoadError(e: Throwable) {
        errorMessage = when(e) {
            is InvoiceException.NetworkError -> R.string.error_network_connection
            is InvoiceException.NotFoundError -> R.string.error_data_not_found
            is InvoiceException.ServerError -> {
                if (e.code == 404) R.string.error_data_not_found
                else R.string.error_server_maintenance
            }
            is InvoiceException.LocalDataError -> R.string.error_local_data
            else -> R.string.error_unknown
        }

        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
        }
    }

    private fun setupDynamicFilterPrices(invoices: List<Invoice>) {
        if (invoices.isEmpty()) return

        val newMin = minInvoiceAmount
        val newMax = maxInvoiceAmount

        val isAtFullRange = abs(currentFilterState.minPrice - lastMinLimit) < 0.1f &&
                            abs(currentFilterState.maxPrice - lastMaxLimit) < 0.1f

        val isFirstLoad = lastMinLimit == 0f && lastMaxLimit == 500f

        if (isFirstLoad || isAtFullRange) {
            currentFilterState = currentFilterState.copy(
                minPrice = newMin,
                maxPrice = newMax
            )
        } else {
            currentFilterState = currentFilterState.copy(
                minPrice = currentFilterState.minPrice.coerceIn(newMin, newMax),
                maxPrice = currentFilterState.maxPrice.coerceIn(newMin, newMax)
            )
        }

        lastMinLimit = newMin
        lastMaxLimit = newMax
    }

    private fun sanitizeFilterDates(invoices: List<Invoice>) {
        if (invoices.isEmpty()) return
        
        val minDateStr = minInvoiceDate ?: return
        val maxDateStr = maxInvoiceDate ?: return
        
        val dataMin = DateMapper.toLocalDate(minDateStr)
        val dataMax = DateMapper.toLocalDate(maxDateStr)

        if (currentFilterState.dateFrom.isNotEmpty()) {
            val currentFrom = DateMapper.toLocalDate(currentFilterState.dateFrom)
            if (currentFrom.isBefore(dataMin) || currentFrom.isAfter(dataMax)) {
                currentFilterState = currentFilterState.copy(dateFrom = "")
            }
        }

        if (currentFilterState.dateTo.isNotEmpty()) {
            val currentTo = DateMapper.toLocalDate(currentFilterState.dateTo)
            if (currentTo.isBefore(dataMin) || currentTo.isAfter(dataMax)) {
                currentFilterState = currentFilterState.copy(dateTo = "")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun updateGasAvailability(enabled: Boolean) {
        val firstLoad = isGasEnabled == null
        isGasEnabled = enabled

        logAnalyticsUseCase("actualizacion_config_remota_gas", mapOf("habilitado" to enabled), priority = AnalyticsPriority.LOW)

        if (firstLoad) {
            loadInvoices()
        } else {
            if (!enabled && selectedTab == 1) {
                selectTab(0)
            } else {
                updateFilteredInvoices()
            }
        }
    }

    private fun updateFilteredInvoices() {
        if (allInvoices.isEmpty() && state is InvoiceListState.LOADING) {
            return
        }

        val contractTypeFilter = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS

        val categoryInvoices = allInvoices.filter { it.contractType == contractTypeFilter }

        if (categoryInvoices.isEmpty()) {
            state = InvoiceListState.SUCCESS(emptyMap(), null)
            return
        }

        val lastInvoice = categoryInvoices.maxByOrNull { DateMapper.toLocalDate(it.issueDate) }

        val filteredInvoices = categoryInvoices.filter { invoice ->
            val matchesSearch = searchQuery.isEmpty() || invoice.id.startsWith(searchQuery, ignoreCase = true)
            val amountFloat = invoice.amount.toFloat()
            val matchesPrice = amountFloat >= currentFilterState.minPrice &&
                    amountFloat <= currentFilterState.maxPrice

            val matchesStatus = currentFilterState.selectedStatuses.isEmpty() ||
                    currentFilterState.selectedStatuses.contains(invoice.status.description)

            val matchesDate = checkDateRange(invoice.issueDate)

            matchesSearch && matchesPrice && matchesStatus && matchesDate
        }

        val secondarySorted = when (currentSortOption) {
            SortOption.DATE -> filteredInvoices.sortedByDescending { DateMapper.toLocalDate(it.issueDate) }
            SortOption.PRICE -> filteredInvoices.sortedByDescending { it.amount }
            SortOption.TYPE -> filteredInvoices.sortedBy { it.status.description }
        }
        val sortedInvoices = secondarySorted.sortedByDescending {
            DateMapper.toLocalDate(it.issueDate).year
        }

        val groupedByYear = sortedInvoices.groupBy { invoice ->
            try {
                DateMapper.toLocalDate(invoice.issueDate).year.toString()
            } catch (e: Exception) {
                "Sin fecha"
            }
        }

        state = InvoiceListState.SUCCESS(groupedByYear, lastInvoice)

    }

    fun registerBackNavigation() {
        viewModelScope.launch {
            checkFeedbackUseCase.notifyBackPress()
        }
    }
    fun selectTab(index: Int) {
        val tipo = if (index == 0) "Luz" else "Gas"
        logAnalyticsUseCase("click_cambio_pestana_facturas", mapOf("tipo" to tipo), priority = AnalyticsPriority.MEDIUM)
        selectedTab = index
        updateFilteredInvoices()
    }
    fun clearErrorMessage() {
        errorMessage = null
    }

    fun handleRefresh() {
        logAnalyticsUseCase("click_refrescar_facturas", priority = AnalyticsPriority.LOW)
        if (hasActiveFilters()) {
            showRefreshDialog = true
        } else {
            refreshInvoices(keepFilters = true)
        }
    }

    fun confirmRefresh(keepFilters: Boolean) {
        logAnalyticsUseCase("click_confirmar_refresco", mapOf("mantener_filtros" to keepFilters), priority = AnalyticsPriority.LOW)
        showRefreshDialog = false
        refreshInvoices(keepFilters = keepFilters)
    }

    fun cancelRefreshDialog() {
        showRefreshDialog = false
    }

    private fun refreshInvoices(keepFilters: Boolean) {
        viewModelScope.launch {
            isRefreshing = true
            fetchRemoteConfig {
                loadInvoices(resetFilters = !keepFilters)
            }
            delay(500)
            isRefreshing = false
        }
    }

    private fun observeFeedback() {
        viewModelScope.launch {
            checkFeedbackUseCase.shouldShowFeedback().collect { show ->
                shouldShowFeedback = show
            }
        }
    }

    fun applyFilters(newFilters: FilterState) {
        logAnalyticsUseCase("click_aplicar_filtros", priority = AnalyticsPriority.MEDIUM)
        currentFilterState = newFilters
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun setSortOption(option: SortOption) {
        logAnalyticsUseCase("click_ordenar_facturas", mapOf("opcion" to option.name), priority = AnalyticsPriority.MEDIUM)
        currentSortOption = option
        updateFilteredInvoices()
    }

    fun selectInvoice(invoice: Invoice) {
        selectedInvoice = invoice
    }

    fun clearFilters() {
        logAnalyticsUseCase("click_limpiar_filtros", priority = AnalyticsPriority.MEDIUM)
        currentFilterState = FilterState(
            minPrice = minInvoiceAmount,
            maxPrice = maxInvoiceAmount
        )
        searchQuery = ""
        updateFilteredInvoices()
        shouldScrollToTop = true
    }

    fun removeStatusFilter(status: String) {
        logAnalyticsUseCase("click_eliminar_filtro_estado", mapOf("estado" to status), priority = AnalyticsPriority.MEDIUM)
        val newStatuses = currentFilterState.selectedStatuses - status
        currentFilterState = currentFilterState.copy(selectedStatuses = newStatuses)
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun removeDateFilter() {
        logAnalyticsUseCase("click_eliminar_filtro_fecha", priority = AnalyticsPriority.MEDIUM)
        currentFilterState = currentFilterState.copy(dateFrom = "", dateTo = "")
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun removePriceFilter() {
        logAnalyticsUseCase("click_eliminar_filtro_precio", priority = AnalyticsPriority.MEDIUM)
        currentFilterState = currentFilterState.copy(
            minPrice = minInvoiceAmount,
            maxPrice = maxInvoiceAmount
        )
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun onScrollHandled() {
        shouldScrollToHistoric = false
    }

    fun onScrollToTopHandled() {
        shouldScrollToTop = false
    }

    fun hasActiveFilters(): Boolean {
        val isDefaultPrice = abs(currentFilterState.minPrice - minInvoiceAmount) < 0.01f &&
                abs(currentFilterState.maxPrice - maxInvoiceAmount) < 0.01f

        return currentFilterState.selectedStatuses.isNotEmpty() ||
                currentFilterState.dateFrom.isNotEmpty() ||
                currentFilterState.dateTo.isNotEmpty() ||
                searchQuery.isNotEmpty() ||
                !isDefaultPrice
    }

    fun openSingleInvoiceDialog() {
        showSingleInvoiceDialog = true
    }

    fun closeSingleInvoiceDialog() {
        showSingleInvoiceDialog = false
    }

    fun getCategoryInvoicesCount(): Int {
        val contractTypeFilter = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS
        return allInvoices.count { it.contractType == contractTypeFilter }
    }

    private fun checkDateRange(invoiceDateStr: String): Boolean {
        if (currentFilterState.dateFrom.isEmpty() && currentFilterState.dateTo.isEmpty()) {
            return true
        }

        return try {
            val invoiceDate = DateMapper.toLocalDate(invoiceDateStr)

            val matchesFrom = if (currentFilterState.dateFrom.isNotEmpty()) {
                val fromDate = DateMapper.toLocalDate(currentFilterState.dateFrom)
                !invoiceDate.isBefore(fromDate)
            } else true

            val matchesTo = if (currentFilterState.dateTo.isNotEmpty()) {
                val toDate = DateMapper.toLocalDate(currentFilterState.dateTo)
                !invoiceDate.isAfter(toDate)
            } else true

            matchesFrom && matchesTo
        } catch (e: Exception) {
            true
        }
    }

    fun toggleAmountVisibility() {
        val nuevoEstado = if (!isAmountVisible) "Visible" else "Oculto"
        logAnalyticsUseCase("click_alternar_visibilidad_importe", mapOf("estado_final" to nuevoEstado), priority = AnalyticsPriority.LOW)
        viewModelScope.launch {
            userPrefs.updateAmountVisibility(!isAmountVisible)
        }
    }
}
