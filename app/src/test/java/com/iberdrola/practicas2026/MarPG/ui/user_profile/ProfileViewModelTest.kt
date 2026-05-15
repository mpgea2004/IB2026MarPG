package com.iberdrola.practicas2026.MarPG.ui.user_profile

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.VerifyUserPasswordUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ProfileValidationResult
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ValidateProfileUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProfileViewModel
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val validateProfile = mockk<ValidateProfileUseCase>(relaxed = true)
    private val verifyUserPassword = mockk<VerifyUserPasswordUseCase>(relaxed = true)

    private val userProfileFlow = MutableStateFlow(ProfileState())

    @Before
    fun setUp() {
        every { userPrefs.userProfileFlow } returns userProfileFlow
        viewModel = ProfileViewModel(
            userPrefs = userPrefs,
            logAnalyticsUseCase = logAnalytics,
            validateProfile = validateProfile,
            verifyUserPasswordUseCase = verifyUserPassword
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `al iniciar el ViewModel se debe cargar el perfil con un delay`() = runTest {
        assertTrue(viewModel.state.isLoading)
        
        advanceUntilIdle()
        
        assertFalse(viewModel.state.isLoading)
        assertTrue(viewModel.state.isEditMode.not())
        verify { logAnalytics("view_perfil_usuario", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `onEditClick debe mostrar dialogo de seguridad si hay contraseña`() = runTest {
        userProfileFlow.value = ProfileState(password = "1234")
        advanceUntilIdle()

        viewModel.onEditClick()

        assertTrue(viewModel.state.showSecurityDialog)
        verify { logAnalytics("click_editar_perfil", priority = AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `onSecurityConfirmClick con password correcto debe habilitar modo edicion tras delay`() = runTest {
        userProfileFlow.value = ProfileState(password = "1234")
        advanceUntilIdle()
        viewModel.onEditClick()
        
        coEvery { verifyUserPassword(any()) } returns true
        
        viewModel.onSecurityConfirmClick()
        
        assertTrue(viewModel.state.isVerifying)
        advanceUntilIdle()
        
        assertFalse(viewModel.state.isVerifying)
        assertTrue(viewModel.state.isEditMode)
        assertFalse(viewModel.state.showSecurityDialog)
        verify { logAnalytics("verificacion_seguridad_correcta", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `saveChanges debe actualizar el perfil tras delay cuando es exitoso`() = runTest {
        advanceUntilIdle()
        viewModel.onNameChange("Mar")
        every { validateProfile(any(), any(), any(), any(), any()) } returns ProfileValidationResult.Success

        var successCalled = false
        viewModel.saveChanges { successCalled = true }

        assertTrue(viewModel.state.isSaving)
        verify { logAnalytics("click_guardar_perfil", priority = AnalyticsPriority.MEDIUM) }
        
        advanceTimeBy(2001)
        
        coVerify { userPrefs.updateProfile(any()) }
        assertTrue(successCalled)
        assertTrue(viewModel.state.isSaved)
        
        advanceTimeBy(2001)
        assertFalse(viewModel.state.isEditMode)
        verify { logAnalytics("exito_guardado_perfil", priority = AnalyticsPriority.HIGH) }
    }

    @Test
    fun `saveChanges debe mostrar errores si la validacion falla`() = runTest {
        advanceUntilIdle()
        every { validateProfile(any(), any(), any(), any(), any()) } returns ProfileValidationResult.Error(
            nameError = 1,
            emailError = 2
        )

        viewModel.saveChanges { }

        assertEquals(1, viewModel.state.nameError)
        assertEquals(2, viewModel.state.emailError)
        coVerify(exactly = 0) { userPrefs.updateProfile(any()) }
        verify { logAnalytics("error_validacion_perfil", any(), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `logout debe limpiar perfil y ejecutar callback`() = runTest {
        advanceUntilIdle()
        var successCalled = false
        
        viewModel.logout { successCalled = true }
        
        coVerify { userPrefs.clearProfile() }
        assertTrue(successCalled)
        verify { logAnalytics("confirmacion_cierre_sesion", priority = AnalyticsPriority.HIGH) }
    }
}
