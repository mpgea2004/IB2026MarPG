package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
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
    private val userPrefs: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<InvoiceListState>(InvoiceListState.LOADING)
        private set

    var userAddress by mutableStateOf("")
        private set

    var shouldShowFeedback by mutableStateOf(false)
        private set

    var selectedTab by mutableIntStateOf(0)
        private set

    var allInvoices : List<Invoice> = emptyList()

    private val isCloud: Boolean = savedStateHandle["isCloud"] ?: false

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
        loadInvoices()
        observeFeedback()
        observeUserProfile()
        observeAmountVisibility()
    }
    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                userAddress = profile.address
            }
        }
    }

    private fun observeAmountVisibility() {
        viewModelScope.launch {
            userPrefs.amountVisibleFlow.collect { visible ->
                isAmountVisible = visible
            }
        }
    }

    private fun loadInvoices() {
        viewModelScope.launch {
            prepareLoadingState()

            getInvoicesUseCase(isCloud)
                .catch { handleLoadError(it) }
                .collect { invoices ->
                    allInvoices = invoices
                    setupDynamicFilterPrices(invoices)

                    if (invoices.isEmpty()) {
                        state = InvoiceListState.NODATA
                    } else {
                        errorMessage = null
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
        }
        
        lastMinLimit = newMin
        lastMaxLimit = newMax
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

    private fun updateFilteredInvoices() {
        if (allInvoices.isEmpty() && state is InvoiceListState.LOADING) {
            return
        }

        val contractTypeFilter = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS

        val categoryInvoices = allInvoices.filter { it.contractType == contractTypeFilter }

        if (categoryInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
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

        val sortedInvoices = when (currentSortOption) {
            SortOption.DATE -> filteredInvoices.sortedByDescending { DateMapper.toLocalDate(it.issueDate) }
            SortOption.PRICE -> filteredInvoices.sortedByDescending { it.amount }
            SortOption.TYPE -> filteredInvoices.sortedBy { it.status.description }
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
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun setSortOption(option: SortOption) {
        currentSortOption = option
        updateFilteredInvoices()
    }

    fun selectInvoice(invoice: Invoice) {
        selectedInvoice = invoice
    }

    fun clearFilters() {
        currentFilterState = FilterState(
            minPrice = minInvoiceAmount,
            maxPrice = maxInvoiceAmount
        )
        searchQuery = ""
        updateFilteredInvoices()
        shouldScrollToTop = true
    }

    fun removeStatusFilter(status: String) {
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
        currentFilterState = currentFilterState.copy(dateFrom = "", dateTo = "")
        updateFilteredInvoices()
        if (hasActiveFilters()) {
            shouldScrollToHistoric = true
        } else {
            shouldScrollToTop = true
        }
    }

    fun removePriceFilter() {
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
        viewModelScope.launch {
            userPrefs.updateAmountVisibility(!isAmountVisible)
        }
    }
}