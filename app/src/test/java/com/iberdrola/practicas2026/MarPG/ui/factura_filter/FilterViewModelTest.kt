package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: FilterViewModel
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)

    @Before
    fun setUp() {
        viewModel = FilterViewModel(logAnalytics)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando se crea el ViewModel se debe enviar el evento de vista de pantalla`() {
        verify { logAnalytics("view_filter_screen", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `al establecer el estado inicial se debe mostrar loading y luego los datos`() = runTest {
        val initialState = FilterState(minPrice = 10f, maxPrice = 50f)
        viewModel.setInitialState(initialState)
        
        assertTrue(viewModel.state.isLoading)
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.isLoading)
        assertEquals(10f, viewModel.state.minPrice)
        assertEquals(50f, viewModel.state.maxPrice)
    }

    @Test
    fun `al cambiar el rango de precios el estado debe redondearse hacia abajo y enviar evento`() {
        viewModel.onPriceRangeChange(10.7f, 100.2f)
        assertEquals(10f, viewModel.state.minPrice)
        assertEquals(100f, viewModel.state.maxPrice)
        verify { logAnalytics("filter_change_price_range", mapOf("min_price" to 10.0, "max_price" to 100.0), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `al cambiar el rango decimal se debe sumar a la base entera del precio minimo`() {
        viewModel.onPriceRangeChange(10f, 100f)
        viewModel.onDecimalRangeChange(0.5f, 0.9f)
        
        assertEquals(10.5f, viewModel.state.minPrice)
        assertEquals(10.9f, viewModel.state.maxPrice)
        verify { logAnalytics("filter_change_price_decimal", any(), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `al cambiar las fechas se deben actualizar y enviar eventos`() {
        viewModel.onDateFromChange("2024-01-01")
        assertEquals("2024-01-01", viewModel.state.dateFrom)
        verify { logAnalytics("filter_select_date_from", mapOf("date" to "2024-01-01"), AnalyticsPriority.MEDIUM) }

        viewModel.onDateToChange("2024-02-01")
        assertEquals("2024-02-01", viewModel.state.dateTo)
        verify { logAnalytics("filter_select_date_to", mapOf("date" to "2024-02-01"), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `al alternar un estado de factura se debe añadir o quitar de la lista de seleccionados`() {
        val statusTest = "Pagada"

        viewModel.onStatusToggle(statusTest)
        assertTrue(viewModel.state.selectedStatuses.contains(statusTest))
        verify { logAnalytics("filter_toggle_status", mapOf("status" to statusTest), AnalyticsPriority.MEDIUM) }

        viewModel.onStatusToggle(statusTest)
        assertFalse(viewModel.state.selectedStatuses.contains(statusTest))
    }

    @Test
    fun `clearFilters debe resetear todos los filtros al estado inicial con prioridad baja`() {
        viewModel.onDateFromChange("2024-01-01")
        viewModel.onStatusToggle("Pendiente")

        viewModel.clearFilters(5f, 200f)

        assertEquals("", viewModel.state.dateFrom)
        assertTrue(viewModel.state.selectedStatuses.isEmpty())
        assertEquals(5f, viewModel.state.minPrice)
        assertEquals(200f, viewModel.state.maxPrice)
        assertFalse(viewModel.state.isLoading)
        verify { logAnalytics("filter_reset_click", priority = AnalyticsPriority.LOW) }
    }

    @Test
    fun `al aplicar filtros se debe enviar evento con el numero de estados seleccionados`() {
        viewModel.onStatusToggle("Pagada")
        viewModel.onStatusToggle("Pendiente")

        viewModel.onApply()

        verify {
            logAnalytics("filter_applied", mapOf("status_count" to 2L), AnalyticsPriority.MEDIUM)
        }
    }
}
