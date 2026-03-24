package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.lifecycle.SavedStateHandle
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ElectronicInvoiceListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: ElectronicInvoiceListViewModel
    private val getElectronicInvoiceUseCase = mockk<GetElectronicInvoiceUseCase>(relaxed = true)
    private val logAnalyticsUseCase = mockk<LogAnalyticsEventUseCase>(relaxed = true)

    private val mockInvoices = listOf(
        ElectronicInvoice(id = "1", isEnabled = false, type = ContractType.LUZ, email = "luz@test.com"),
        ElectronicInvoice(id = "2", isEnabled = true, type = ContractType.GAS, email = "gas@test.com")
    )

    @Before
    fun setUp() {
        every { getElectronicInvoiceUseCase(any()) } returns flowOf(mockInvoices)

        viewModel = ElectronicInvoiceListViewModel(
            getElectronicInvoiceUseCase = getElectronicInvoiceUseCase,
            savedStateHandle = SavedStateHandle(mapOf("isCloud" to true)),
            logAnalyticsUseCase = logAnalyticsUseCase
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `al iniciar el ViewModel se deben cargar los contratos y mostrar SUCCESS`() = runTest {
        advanceUntilIdle()

        val currentState = viewModel.state

        assertTrue("El estado debería ser Success", currentState is ElectronicInvoiceListState.Success)

        if (currentState is ElectronicInvoiceListState.Success) {
            assertEquals(2, currentState.contracts.size)
        }

        verify { logAnalyticsUseCase("view_electronic_invoice_selection") }
    }

    @Test
    fun `cuando la carga falla se debe mostrar el estado de error y loguear en analytics`() = runTest {
        val errorMsg = "Error de red"
        every { getElectronicInvoiceUseCase(any()) } returns flow { throw Exception(errorMsg) }

        viewModel.onRetryClicked()
        advanceUntilIdle()

        assertTrue(viewModel.state is ElectronicInvoiceListState.Error)
        assertEquals(errorMsg, (viewModel.state as ElectronicInvoiceListState.Error).message)
        verify { logAnalyticsUseCase("elec_invoice_load_error", mapOf("error" to errorMsg)) }
    }

    @Test
    fun `al hacer click en un contrato se debe enviar el evento de seleccion a analytics`() {
        val invoice = mockInvoices[0]

        viewModel.onElectronicInvoiceClick(invoice)

        verify {
            logAnalyticsUseCase("elec_invoice_selected", mapOf(
                "id" to "1",
                "type" to "LUZ"
            ))
        }
    }

    @Test
    fun `el boton de volver debe registrar la accion en analytics`() {
        viewModel.onBackClicked()

        verify { logAnalyticsUseCase("elec_invoice_back_click") }
    }
}