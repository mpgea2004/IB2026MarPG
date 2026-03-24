package com.iberdrola.practicas2026.MarPG.ui.user_profile

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidatePhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ProfileValidationResult
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ValidateProfileUseCase
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.MainDispatcherRule
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private val validateEmail = mockk<ValidateEmailUseCase>(relaxed = true)
    private val validatePhone = mockk<ValidatePhoneUseCase>(relaxed = true)

    @Before
    fun setUp() {
        every { userPrefs.userProfileFlow } returns flowOf(ProfileState())

        viewModel = ProfileViewModel(
            userPrefs = userPrefs,
            logAnalyticsUseCase = logAnalytics,
            validateProfile = validateProfile,
            validateEmailUseCase = validateEmail,
            validatePhoneUseCase = validatePhone
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `isSaveEnabled debe ser falso si el nombre esta vacio`() {
        viewModel.onNameChange("")
        assertFalse(viewModel.isSaveEnabled)
    }

    @Test
    fun `isSaveEnabled debe ser verdadero si los datos son validos`() {
        viewModel.onNameChange("Mar")
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true

        viewModel.onEmailChange("mar@test.com")
        viewModel.onPhoneChange("600000000")

        assertTrue(viewModel.isSaveEnabled)
    }


    @Test
    fun `saveChanges debe actualizar el perfil y ejecutar el callback de exito cuando los datos son correctos`() = runTest {
        viewModel.onNameChange("Mar")
        every { validateProfile(any(), any()) } returns ProfileValidationResult.Success

        var callbackLlamado = false
        val onSuccess = { callbackLlamado = true }

        viewModel.saveChanges(onSuccess)

        coVerify { userPrefs.updateProfile(any()) }
        verify { logAnalytics("profile_save_success") }
        assertTrue(callbackLlamado)
    }


    @Test
    fun `saveChanges debe mostrar errores y no guardar si la validacion falla`() = runTest {
        val errorEmail = "Email incorrecto"
        every { validateProfile(any(), any()) } returns ProfileValidationResult.Error(
            emailError = errorEmail,
            phoneError = null
        )

        var callbackLlamado = false

        viewModel.saveChanges { callbackLlamado = true }

        coVerify(exactly = 0) { userPrefs.updateProfile(any()) }
        assertEquals(errorEmail, viewModel.state.emailError)
        assertFalse(callbackLlamado)
        verify { logAnalytics("profile_save_error", any()) }
    }

    @Test
    fun `onEmailChange debe actualizar el mensaje de error si el email no es valido`() {
        every { validateEmail("email_malo") } returns false

        viewModel.onEmailChange("email_malo")

        assertEquals("Formato de email inválido", viewModel.state.emailError)
    }
}