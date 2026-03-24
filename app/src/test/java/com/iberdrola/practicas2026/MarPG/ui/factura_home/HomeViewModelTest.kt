package com.iberdrola.practicas2026.MarPG.ui.factura_home

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
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

    private val userProfileFlow = MutableStateFlow(ProfileState(name = "Mar"))
    private val feedbackFlow = MutableStateFlow(false)

    @Before
    fun setUp() {
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
        clearAllMocks()
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

        assertEquals("Usuario", viewModel.state.userName)
    }

    @Test
    fun `al activar el modo nube se debe actualizar el estado y enviar evento a analytics`() {
        viewModel.events.onToggleCloud(true)

        assertTrue(viewModel.state.isCloudEnabled)
        verify { logAnalytics("config_data_source", mapOf("modo" to "nube")) }

        viewModel.events.onToggleCloud(false)
        assertFalse(viewModel.state.isCloudEnabled)
        verify { logAnalytics("config_data_source", mapOf("modo" to "local")) }
    }

    @Test
    fun `cuando se selecciona una opcion de feedback se debe llamar al caso de uso con los dias correctos`() = runTest {
        viewModel.events.onFeedbackOption(10)

        coVerify { checkFeedbackUseCase.setNextTregua(10) }
        verify { logAnalytics("click_feedback_option", mapOf("option" to "valorar")) }

        viewModel.events.onFeedbackOption(3)
        coVerify { checkFeedbackUseCase.setNextTregua(3) }
        verify { logAnalytics("click_feedback_option", mapOf("option" to "luego")) }
    }

    @Test
    fun `cuando el caso de uso indica que hay que mostrar feedback el BottomSheet debe hacerse visible`() = runTest {
        feedbackFlow.value = true
        advanceUntilIdle()

        assertTrue(viewModel.state.isSheetVisible)
    }
}