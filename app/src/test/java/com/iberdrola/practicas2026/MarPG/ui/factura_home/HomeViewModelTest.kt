package com.iberdrola.practicas2026.MarPG.ui.factura_home

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.every
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private val checkFeedbackUseCase = mockk<CheckFeedbackUseCase>(relaxed = true)
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val remoteConfig = mockk<FirebaseRemoteConfig>(relaxed = true)

    private val userProfileFlow = MutableStateFlow(ProfileState(name = "Mar"))
    private val feedbackFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
        mockkStatic("com.google.firebase.FirebaseKt")
        mockkStatic("com.google.firebase.remoteconfig.RemoteConfigKt")
        
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
        every { remoteConfig.getBoolean(any()) } returns true

        every { userPrefs.userProfileFlow } returns userProfileFlow
        every { checkFeedbackUseCase.shouldShowFeedback() } returns feedbackFlow

        viewModel = HomeViewModel(
            checkFeedbackUseCase = checkFeedbackUseCase,
            userPrefs = userPrefs,
            logAnalyticsUseCase = logAnalytics
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando el perfil cambia el nombre en la Home debe actualizarse automaticamente`() = runTest {
        advanceUntilIdle()
        assertEquals("Mar", viewModel.state.userName)

        userProfileFlow.value = ProfileState(name = "Iberdrola")
        advanceUntilIdle()

        assertEquals("Iberdrola", viewModel.state.userName)
    }

    @Test
    fun `si el nombre del perfil esta vacio la Home debe mostrar Usuario por defecto`() = runTest {
        userProfileFlow.value = ProfileState(name = "")
        advanceUntilIdle()

        assertEquals("", viewModel.state.userName)
    }

    @Test
    fun `cuando se selecciona una opcion de feedback se debe llamar al caso de uso con los dias correctos`() = runTest {
        viewModel.onOptionSelected(10)

        advanceUntilIdle()
        coVerify { checkFeedbackUseCase.setNextTregua(10) }
        verify { logAnalytics("click_feedback_opcion", mapOf("puntuacion" to 10), AnalyticsPriority.HIGH) }
        assertTrue(viewModel.state.isFeedbackSubmitted)

        viewModel.onOptionSelected(3)
        advanceUntilIdle()
        coVerify { checkFeedbackUseCase.setNextTregua(3) }
        verify { logAnalytics("click_feedback_opcion", mapOf("puntuacion" to 3), AnalyticsPriority.HIGH) }
        assertFalse(viewModel.state.isSheetVisible)
    }

    @Test
    fun `cuando el caso de uso indica que hay que mostrar feedback el BottomSheet debe hacerse visible`() = runTest {
        feedbackFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.state.isSheetVisible)
        verify { logAnalytics("view_feedback_sheet", priority = AnalyticsPriority.LOW) }
    }

    @Test
    fun `al cerrar el sheet el estado debe actualizarse`() {
        viewModel.onCloseSheet()
        assertFalse(viewModel.state.isSheetVisible)
    }

    @Test
    fun `al pulsar en no volver a preguntar se debe llamar al caso de uso y cerrar el sheet`() = runTest {
        viewModel.onDontAskAgain()
        advanceUntilIdle()
        coVerify { checkFeedbackUseCase.dontAskAgain() }
        assertFalse(viewModel.state.isSheetVisible)
        verify { logAnalytics("click_feedback_no_preguntar_otra", priority = AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `navegar con check de perfil debe mostrar dialogo si no esta completo`() = runTest {
        userProfileFlow.value = ProfileState(name = "Mar", email = "")
        advanceUntilIdle()
        
        var navigated = false
        viewModel.onNavigateWithProfileCheck { navigated = true }

        assertTrue(viewModel.state.showGuestDialog)
        assertNotNull(viewModel.state.pendingNavigation)
        assertFalse(navigated)
    }

    @Test
    fun `al confirmar invitado se debe actualizar el perfil y ejecutar navegacion pendiente`() = runTest {
        var navigated = false
        viewModel.onNavigateWithProfileCheck { navigated = true }
        
        viewModel.onConfirmGuest()
        
        advanceUntilIdle()
        coVerify { userPrefs.updateProfile(any()) }
        assertFalse(viewModel.state.showGuestDialog)
        assertTrue(navigated)
        assertNull(viewModel.state.pendingNavigation)
        verify { logAnalytics("click_confirmar_predeterminado", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `al cancelar dialogo de invitado se debe limpiar el estado`() {
        viewModel.onNavigateWithProfileCheck { }
        
        viewModel.onDismissGuestDialog()
        
        assertFalse(viewModel.state.showGuestDialog)
        assertNull(viewModel.state.pendingNavigation)
        verify { logAnalytics("click_cancelar_predeterminado", priority = AnalyticsPriority.LOW) }
    }
}
