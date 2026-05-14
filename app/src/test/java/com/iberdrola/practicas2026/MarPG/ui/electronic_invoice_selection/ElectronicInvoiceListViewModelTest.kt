package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
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

    private val getElectronicInvoiceUseCase = mockk<GetElectronicInvoiceUseCase>(relaxed = true)
    private val logAnalyticsUseCase = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)

    private lateinit var viewModel: ElectronicInvoiceListViewModel

    private val mockInvoices = listOf(
        ElectronicInvoice(id = "1", isEnabled = false, type = ContractType.LUZ, email = "luz@test.com"),
        ElectronicInvoice(id = "2", isEnabled = true, type = ContractType.GAS, email = "gas@test.com")
    )

    @Before
    fun setUp() {
        mockkStatic("com.google.firebase.remoteconfig.RemoteConfigKt")
        mockkStatic("com.google.firebase.FirebaseKt")
        
        every { Firebase.remoteConfig } returns remoteConfig
        
        val mockTask = mockk<Task<Boolean>>(relaxed = true)
        every { remoteConfig.fetchAndActivate() } returns mockTask
        every { remoteConfig.setDefaultsAsync(any<Map<String, Any>>()) } returns mockk(relaxed = true)
        every { remoteConfig.setConfigSettingsAsync(any()) } returns mockk(relaxed = true)
        
        val slot = slot<OnCompleteListener<Boolean>>()
        every { mockTask.addOnCompleteListener(capture(slot)) } answers {
            slot.captured.onComplete(mockTask)
            mockTask
        }
        every { mockTask.isSuccessful } returns true
        every { remoteConfig.getBoolean("show_gas_contracts") } returns true

        every { getElectronicInvoiceUseCase(any()) } returns flowOf(mockInvoices, mockInvoices)

        viewModel = ElectronicInvoiceListViewModel(
            getElectronicInvoiceUseCase = getElectronicInvoiceUseCase,
            savedStateHandle = SavedStateHandle(mapOf("isCloud" to true)),
            logAnalyticsUseCase = logAnalyticsUseCase
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al iniciar el ViewModel se deben cargar los contratos tras el delay del principio`() = runTest {
        advanceTimeBy(2001)
        advanceUntilIdle()

        val currentState = viewModel.state

        assertTrue(currentState is ElectronicInvoiceListState.Success)

        if (currentState is ElectronicInvoiceListState.Success) {
            assertEquals(2, currentState.contracts.size)
        }

        verify { logAnalyticsUseCase("view_seleccion_factura_electronica", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `cuando la carga falla se debe mostrar mensaje de error y loguear en analyhtics`() = runTest {
        val errorMsg = "Error de red"
        every { getElectronicInvoiceUseCase(any()) } returns flow { throw Exception(errorMsg) }

        viewModel.loadInvoices()
        advanceTimeBy(2001)
        advanceUntilIdle()

        assertEquals(com.iberdrola.practicas2026.MarPG.R.string.error_unknown, viewModel.errorMessage)
        verify { logAnalyticsUseCase("error_carga_facturas_electronicas", mapOf("error" to errorMsg), AnalyticsPriority.HIGH) }
    }

    @Test
    fun `al hacer click en un contrato se debe enviar el evento de seleccion a analytics`() {
        val invoice = mockInvoices[0]

        viewModel.onElectronicInvoiceClick(invoice)

        verify {
            logAnalyticsUseCase("click_seleccionar_factura", mapOf(
                "id" to "1",
                "tipo" to "LUZ"
            ), AnalyticsPriority.MEDIUM)
        }
    }

    @Test
    fun `el boton de volver debe registrar la accion en analytics`() {
        viewModel.onBackClicked()

        verify { logAnalyticsUseCase("click_volver_seleccion_electronica", priority = AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `refreshInvoices debe activar el estado de refresco y registrar el evento`() {
        viewModel.refreshInvoices()
        
        assertTrue(viewModel.isRefreshing)
        verify { logAnalyticsUseCase("click_refrescar_seleccion_electronica", priority = AnalyticsPriority.LOW) }
    }
}
