package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.data.network.InvoiceException
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.invoice.GetInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterState
import junit.framework.TestCase.assertTrue
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.every
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
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
class InvoiceListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val checkFeedbackUseCase = mockk<CheckFeedbackUseCase>(relaxed = true)
    private val getInvoicesUseCase = mockk<GetInvoiceUseCase>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)

    private lateinit var viewModel: InvoiceListViewModel

    private val mockInvoices = listOf(
        Invoice(
            id = "1",
            amount = 50.0,
            contractType = ContractType.LUZ,
            issueDate = "01/01/2024",
            status = InvoiceStatus.PAGADAS,
            startDate = "01/12/2023",
            endDate = "31/12/2023"
        ),
        Invoice(
            id = "2",
            amount = 120.0,
            contractType = ContractType.GAS,
            issueDate = "15/12/2023",
            status = InvoiceStatus.PENDIENTES_PAGO,
            startDate = "01/11/2023",
            endDate = "30/11/2023"
        )
    )

    @Before
    fun setUp() {
        mockkStatic("com.google.firebase.FirebaseKt")
        mockkStatic("com.google.firebase.remoteconfig.RemoteConfigKt")
        mockkStatic(FirebaseCrashlytics::class)
        
        val mockCrashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        every { FirebaseCrashlytics.getInstance() } returns mockCrashlytics
        
        every { Firebase.remoteConfig } returns remoteConfig
        
        val mockTask = mockk<Task<Boolean>>(relaxed = true)
        val mockVoidTask = mockk<Task<Void>>(relaxed = true)
        
        every { remoteConfig.setDefaultsAsync(any<Map<String, Any>>()) } returns mockVoidTask
        val voidSlot = slot<OnCompleteListener<Void>>()
        every { mockVoidTask.addOnCompleteListener(capture(voidSlot)) } answers {
            voidSlot.captured.onComplete(mockVoidTask)
            mockVoidTask
        }

        every { remoteConfig.fetchAndActivate() } returns mockTask
        val boolSlot = slot<OnCompleteListener<Boolean>>()
        every { mockTask.addOnCompleteListener(capture(boolSlot)) } answers {
            boolSlot.captured.onComplete(mockTask)
            mockTask
        }
        
        every { remoteConfig.setConfigSettingsAsync(any()) } returns mockVoidTask
        every { remoteConfig.addOnConfigUpdateListener(any()) } returns mockk(relaxed = true)

        every { mockTask.isSuccessful } returns true
        every { remoteConfig.getBoolean("show_gas_contracts") } returns true

        every { userPrefs.userProfileFlow } returns flowOf(mockk(relaxed = true))
        every { userPrefs.amountVisibleFlow } returns flowOf(true)
        every { checkFeedbackUseCase.shouldShowFeedback() } returns flowOf(false)
        every { getInvoicesUseCase(any()) } returns flowOf(mockInvoices)

        viewModel = InvoiceListViewModel(
            checkFeedbackUseCase,
            getInvoicesUseCase,
            logAnalytics,
            userPrefs,
            SavedStateHandle(mapOf("isCloud" to true))
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `al iniciar el ViewModel se deben cargar las facturas y agrupar por año`() = runTest {
        advanceTimeBy(301)
        advanceUntilIdle()

        assertTrue(viewModel.state is InvoiceListState.SUCCESS)
        val successState = viewModel.state as InvoiceListState.SUCCESS
        assertTrue(successState.groupedInvoices.containsKey("2024"))
        verify { logAnalytics("view_lista_facturas", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `cuando hay un error de red se debe mostrar el mensaje de error correspondiente`() = runTest {
        every { getInvoicesUseCase(any()) } returns flow { throw InvoiceException.NetworkError }

        viewModel.confirmRefresh(keepFilters = false)
        advanceTimeBy(301)
        advanceUntilIdle()

        assertEquals(com.iberdrola.practicas2026.MarPG.R.string.error_network_connection, viewModel.errorMessage)
        verify { logAnalytics("error_carga_facturas", any(), AnalyticsPriority.HIGH) }
    }

    @Test
    fun `al cambiar de pestaña se deben filtrar las facturas por tipo de contrato`() = runTest {
        advanceTimeBy(301)
        advanceUntilIdle()

        viewModel.selectTab(1)

        val successState = viewModel.state as InvoiceListState.SUCCESS
        val invoices = successState.groupedInvoices.values.flatten()
        assertTrue(invoices.all { it.contractType == ContractType.GAS })
        verify { logAnalytics("click_cambio_pestana_facturas", mapOf("tipo" to "Gas"), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `applyFilters debe actualizar el estado y filtrar las facturas`() = runTest {
        advanceTimeBy(301)
        advanceUntilIdle()

        val filter = FilterState(selectedStatuses = setOf("Pagada"))
        viewModel.applyFilters(filter)

        val successState = viewModel.state as InvoiceListState.SUCCESS
        val invoices = successState.groupedInvoices.values.flatten()
        assertTrue(invoices.all { it.status.description == "Pagada" })
        verify { logAnalytics("click_aplicar_filtros", priority = AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `toggleAmountVisibility debe llamar al repositorio y loguear evento`() = runTest {
        viewModel.toggleAmountVisibility()
        
        advanceUntilIdle()
        coVerify { userPrefs.updateAmountVisibility(any()) }
        verify { logAnalytics("click_alternar_visibilidad_importe", mapOf("estado_final" to "Oculto"), AnalyticsPriority.LOW) }
    }

    @Test
    fun `clearFilters debe resetear los filtros al rango original de las facturas`() = runTest {
        advanceTimeBy(301)
        advanceUntilIdle()
        
        viewModel.clearFilters()
        
        assertEquals(50f, viewModel.currentFilterState.minPrice)
        assertEquals(120f, viewModel.currentFilterState.maxPrice)
        assertTrue(viewModel.currentFilterState.selectedStatuses.isEmpty())
        verify { logAnalytics("click_limpiar_filtros", priority = AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `al pulsar en refrescar con filtros activos debe mostrar dialogo de confirmacion`() {
        viewModel.applyFilters(FilterState(selectedStatuses = setOf("Pagada")))
        
        viewModel.handleRefresh()
        
        assertTrue(viewModel.showRefreshDialog)
        verify { logAnalytics("click_refrescar_facturas", priority = AnalyticsPriority.LOW) }
    }
}
