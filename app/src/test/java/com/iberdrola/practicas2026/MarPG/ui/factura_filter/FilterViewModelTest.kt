package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun `al cambiar el rango de precios el estado debe actualizarse correctamente`() {
        viewModel.onPriceRangeChange(10f, 100f)
        assertEquals(10f, viewModel.state.minPrice)
        assertEquals(100f, viewModel.state.maxPrice)
    }

    @Test
    fun `al alternar un estado de factura se debe añadir o quitar de la lista de seleccionados`() {
        val statusTest = "Pagada"

        viewModel.onStatusToggle(statusTest)
        assertTrue(viewModel.state.selectedStatuses.contains(statusTest))
        verify { logAnalytics("filter_toggle_status", any()) }

        viewModel.onStatusToggle(statusTest)
        assertFalse(viewModel.state.selectedStatuses.contains(statusTest))
    }

    @Test
    fun `clearFilters debe resetear todos los filtros al estado inicial`() {
        viewModel.onDateFromChange("2024-01-01")
        viewModel.onStatusToggle("Pendiente")

        viewModel.clearFilters(0f, 500f)

        assertEquals("", viewModel.state.dateFrom)
        assertTrue(viewModel.state.selectedStatuses.isEmpty())
        assertEquals(0f, viewModel.state.minPrice)
        assertEquals(500f, viewModel.state.maxPrice)
        verify { logAnalytics("filter_reset_click", any()) }
    }

    @Test
    fun `al aplicar filtros se debe enviar un evento detallado a analytics`() {
        viewModel.onPriceRangeChange(20f, 80f)
        viewModel.onStatusToggle("Pagada")

        viewModel.onApply()

        verify {
            logAnalytics("filter_applied", any())
        }
    }
}