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
import kotlin.math.ceil


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

    var isRefreshing by mutableStateOf(false)
        private set

    var selectedInvoice by mutableStateOf<Invoice?>(null)
        private set

    val minInvoiceAmount: Float
        get() = allInvoices.minOfOrNull { it.amount.toFloat() } ?: 0f

    val maxInvoiceAmount: Float
        get() = allInvoices.maxOfOrNull { it.amount.toFloat() }?.let { ceil(it) } ?: 500f

    init {
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

    private fun loadInvoices() {
        viewModelScope.launch {
            prepareLoadingState()

            getInvoicesUseCase(isCloud)
                .catch { handleLoadError(it) }
                .collect { invoices ->
                    allInvoices = invoices

                    setupInitialFilterPrices(invoices)

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
        errorMessage = when(e) {
            is InvoiceException.NetworkError -> R.string.error_unexpected
            is InvoiceException.ServerError -> R.string.error_unexpected
            is InvoiceException.LocalDataError -> R.string.error_unknown
            else -> R.string.error_unknown
        }
        
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

    private fun updateFilteredInvoices() {
        if (allInvoices.isEmpty() && state is InvoiceListState.LOADING) {
            return
        }

        if (allInvoices.isEmpty()) {
            state = InvoiceListState.NODATA
            return
        }

        val contractTypeFilter = if (selectedTab == 0) ContractType.LUZ else ContractType.GAS

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

    fun selectInvoice(invoice: Invoice) {
        selectedInvoice = invoice
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