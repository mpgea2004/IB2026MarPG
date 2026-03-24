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
        viewModel.events.onPriceRangeChange(10f, 100f)
        assertEquals(10f, viewModel.state.minPrice)
        assertEquals(100f, viewModel.state.maxPrice)
    }

    @Test
    fun `al alternar un estado de factura se debe añadir o quitar de la lista de seleccionados`() {
        val statusTest = "Pagada"

        viewModel.events.onStatusToggle(statusTest)
        assertTrue(viewModel.state.selectedStatuses.contains(statusTest))
        verify { logAnalytics("filter_toggle_status", mapOf("status" to statusTest)) }

        viewModel.events.onStatusToggle(statusTest)
        assertFalse(viewModel.state.selectedStatuses.contains(statusTest))
    }

    @Test
    fun `onClear debe resetear todos los filtros al estado inicial`() {
        viewModel.events.onDateFromChange("2024-01-01")
        viewModel.events.onStatusToggle("Pendiente")

        viewModel.events.onClear()

        assertEquals("", viewModel.state.dateFrom)
        assertTrue(viewModel.state.selectedStatuses.isEmpty())
        verify { logAnalytics("filter_reset_click", any()) }
    }

    @Test
    fun `onClearWithLimits debe resetear filtros pero manteniendo los limites de precio`() {
        viewModel.onClearWithLimits(5f, 500f)

        assertEquals(5f, viewModel.state.minPrice)
        assertEquals(500f, viewModel.state.maxPrice)
        assertEquals("", viewModel.state.dateFrom)
    }

    @Test
    fun `al aplicar filtros se debe enviar un evento detallado a analytics`() {
        viewModel.events.onPriceRangeChange(20f, 80f)
        viewModel.events.onStatusToggle("Pagada")

        viewModel.events.onApply()

        verify {
            logAnalytics("filter_applied", mapOf(
                "min_price" to 20.0,
                "max_price" to 80.0,
                "status_count" to 1L
            ))
        }
    }
}