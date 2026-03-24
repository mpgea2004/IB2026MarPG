package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.lifecycle.SavedStateHandle
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InvoiceListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: InvoiceListViewModel
    private val checkFeedbackUseCase = mockk<CheckFeedbackUseCase>(relaxed = true)
    private val getInvoicesUseCase = mockk<GetInvoiceUseCase>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)

    private val mockInvoices = listOf(
        Invoice(
            id = "1",
            amount = 50.0,
            contractType = ContractType.LUZ,
            issueDate = "2024-01-01",
            status = mockk(relaxed = true),
            startDate = "2023-12-01",
            endDate = "2023-12-31"
        ),
        Invoice(
            id = "2",
            amount = 120.0,
            contractType = ContractType.GAS,
            issueDate = "2023-12-15",
            status = mockk(relaxed = true),
            startDate = "2023-11-01",
            endDate = "2023-11-30"
        )
    )

    @Before
    fun setUp() {
        every { userPrefs.userProfileFlow } returns flowOf(mockk(relaxed = true))
        every { checkFeedbackUseCase.shouldShowFeedback() } returns flowOf(false)

        every { getInvoicesUseCase(any()) } returns flowOf(mockInvoices)

        viewModel = InvoiceListViewModel(
            checkFeedbackUseCase,
            getInvoicesUseCase,
            logAnalytics,
            userPrefs,
            SavedStateHandle(mapOf("isCloud" to false))
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `al iniciar el ViewModel se deben cargar las facturas y pasar a estado SUCCESS`() = runTest {

        assertTrue("El estado debería ser SUCCESS", viewModel.state is InvoiceListState.SUCCESS)
        assertEquals(2, viewModel.allInvoices.size)
        verify { logAnalytics("invoice_load_success", any()) }
    }

    @Test
    fun `cuando hay un error de red se debe mostrar el mensaje de error correspondiente`() = runTest {
        val errorMsg = "No hay conexión"
        every { getInvoicesUseCase(any()) } returns flow {
            throw InvoiceException.NetworkError
        }

        viewModel.refreshInvoices()

        assertEquals(InvoiceException.NetworkError.message, viewModel.errorMessage)
        verify { logAnalytics("invoice_load_error", any()) }
    }

    @Test
    fun `al cambiar de pestaña se deben filtrar las facturas correctamente`() {

        viewModel.selectTab(1)

        val successState = viewModel.state as InvoiceListState.SUCCESS
        val allFiltered = successState.groupedInvoices.values.flatten()
        assertTrue(allFiltered.all { it.contractType == ContractType.GAS })
    }

    @Test
    fun `si el GAS esta deshabilitado aunque cambie de pestaña debe mostrar LUZ`() {
        viewModel.updateGasAvailability(false)

        viewModel.selectTab(1)

        val successState = viewModel.state as InvoiceListState.SUCCESS
        val allFiltered = successState.groupedInvoices.values.flatten()
        assertTrue(allFiltered.all { it.contractType == ContractType.LUZ })
    }
}