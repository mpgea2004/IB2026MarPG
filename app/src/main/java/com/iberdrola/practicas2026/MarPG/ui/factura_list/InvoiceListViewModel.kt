package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
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
import kotlin.math.ceil


/** Lógica de carga, filtrado y agrupación de facturas por tipo y año */
@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val getInvoicesUseCase: GetInvoiceUseCase,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val userPrefs: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<InvoiceListState>(InvoiceListState.LOADING)
        private set

    var isGasEnabled by mutableStateOf(true)
        private set

    var userAddress by mutableStateOf("")
        private set

    var shouldShowFeedback by mutableStateOf(false)
        private set

    var selectedTab by mutableIntStateOf(0)
        private set

    var allInvoices : List<Invoice> = emptyList()

    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var currentFilterState by mutableStateOf(FilterState())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    val minInvoiceAmount: Float
        get() = allInvoices.minOfOrNull { it.amount.toFloat() } ?: 0f

    val maxInvoiceAmount: Float
        get() = allInvoices.maxOfOrNull { it.amount.toFloat() }?.let { ceil(it) } ?: 500f

    init {
        logAnalyticsUseCase("view_invoice_list_mar")
        loadInvoices()
        observeFeedback()
        observeUserProfile()
    }
    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                userAddress = profile.address
            }
        }
    }


    fun onFilterClicked() {
        logAnalyticsUseCase("click_filter_invoices", mapOf("source" to "button_header"))
    }

    fun onInvoiceClick(invoice: Invoice) {
        logAnalyticsUseCase("click_invoice_detail", mapOf(
            "invoice_id" to invoice.id,
            "contract_type" to invoice.contractType.name,
            "amount" to invoice.amount.toString()
        ))
    }
    private fun loadInvoices() {
        viewModelScope.launch {
            prepareLoadingState()

            getInvoicesUseCase(isCloud)
                .catch { e->
                    handleLoadError(e)
                    logAnalyticsUseCase("invoice_load_error", mapOf("message" to (e.message ?: "unknown")))}
                .collect { invoices ->
                    allInvoices = invoices

                    setupInitialFilterPrices(invoices)
                    logAnalyticsUseCase("invoice_load_success", mapOf("count" to invoices.size))

                    if (invoices.isNotEmpty()) {
                        errorMessage = null
                    }

                    updateFilteredInvoices()
                }
        }
    }

    private fun prepareLoadingState() {
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.LOADING
        }
        errorMessage = null
    }

    private fun handleLoadError(e: Throwable) {
        errorMessage = if (e is InvoiceException) e.message else InvoiceException.Unknown.message
        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
        }
    }

    private fun setupInitialFilterPrices(invoices: List<Invoice>) {
        val isFilterDefault = currentFilterState.minPrice == 0f && currentFilterState.maxPrice == 500f

        if (invoices.isNotEmpty() && isFilterDefault) {
            currentFilterState = currentFilterState.copy(
                minPrice = minInvoiceAmount,
                maxPrice = maxInvoiceAmount
            )
        }
    }

    fun updateGasAvailability(enabled: Boolean) {
        if (isGasEnabled != enabled) {
            isGasEnabled = enabled

            logAnalyticsUseCase("remote_config_gas_updated", mapOf("enabled" to enabled))
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

        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
            return
        }

        val contractTypeFilter = if (selectedTab == 0 || !isGasEnabled) {
            ContractType.LUZ
        } else {
            ContractType.GAS
        }

        val filteredInvoices = allInvoices.filter { invoice ->
            val matchesType = invoice.contractType == contractTypeFilter

            val matchesPrice = invoice.amount >= currentFilterState.minPrice &&
                    invoice.amount <= currentFilterState.maxPrice

            val matchesStatus = currentFilterState.selectedStatuses.isEmpty() ||
                    currentFilterState.selectedStatuses.contains(invoice.status.description)

            val matchesDate = checkDateRange(invoice.issueDate)

            matchesType && matchesPrice && matchesStatus && matchesDate
        }

        val groupedByYear = filteredInvoices.groupBy { invoice ->
            try {
                DateMapper.toLocalDate(invoice.issueDate).year.toString()
            } catch (e: Exception) {
                "Sin fecha"
            }
        }

        state = InvoiceListState.SUCCESS(groupedByYear)

    }

    fun registerBackNavigation() {
        viewModelScope.launch {
            checkFeedbackUseCase.notifyBackPress()
        }
    }
    fun selectTab(index: Int) {
        selectedTab = index
        updateFilteredInvoices()
    }
    fun clearErrorMessage() {
        errorMessage = null
    }

    fun refreshInvoices() {
        viewModelScope.launch {
            isRefreshing = true
            loadInvoices()
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
        currentFilterState = newFilters
        updateFilteredInvoices()
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
}