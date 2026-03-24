package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.*
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
class ElectronicInvoiceViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: ElectronicInvoiceViewModel

    private val updateUseCase = mockk<UpdateElectronicInvoiceUseCase>(relaxed = true)
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val verifyPassword = mockk<VerifyUserPasswordUseCase>(relaxed = true)
    private val updatePhone = mockk<UpdateUserPhoneUseCase>(relaxed = true)
    private val formatPhone = mockk<FormatUserPhoneUseCase>(relaxed = true)
    private val validateEmail = mockk<ValidateEmailUseCase>(relaxed = true)

    @Before
    fun setUp() {
        every { userPrefs.userProfileFlow } returns flowOf(ProfileState())

        viewModel = ElectronicInvoiceViewModel(
            updateUseCase = updateUseCase,
            userPrefs = userPrefs,
            logAnalyticsUseCase = logAnalytics,
            verifyUserPasswordUseCase = verifyPassword,
            updateUserPhoneUseCase = updatePhone,
            formatUserPhoneUseCase = formatPhone,
            validateEmailUseCase = validateEmail
        )
    }

    @Test
    fun `cuando el checkbox legal no esta marcado el boton de continuar debe estar deshabilitado`() {
        viewModel.updateStep(ElectronicInvoiceStep.FORM)

        viewModel.events.onLegalCheckChange(false)

        assertFalse("El botón debería estar deshabilitado si no se aceptan los términos", viewModel.isNextEnabled)
    }

    @Test
    fun `en el paso de OTP el boton solo se habilita si el codigo tiene exactamente 6 digitos`() {
        viewModel.updateStep(ElectronicInvoiceStep.VERIFICATION)

        viewModel.events.onOtpChange("12345")
        assertFalse(viewModel.isNextEnabled)

        viewModel.events.onOtpChange("123456")
        assertTrue(viewModel.isNextEnabled)
    }

    @Test
    fun `cuando falla la actualizacion de la factura se debe capturar el mensaje de error`() {
        val contrato = ElectronicInvoice(
            id = "1",
            isEnabled = false,
            type = ContractType.LUZ,
            email = "test@iberdrola.es"
        )
        viewModel.selectContract(contrato)

        val mensajeError = "Error de conexión con Iberdrola"
        coEvery { updateUseCase(any()) } throws Exception(mensajeError)

        viewModel.performUpdate()

        assertEquals(mensajeError, viewModel.state.error)
        assertFalse("isLoading debería ser false tras el error", viewModel.state.isLoading)
    }

    @Test
    fun `cuando la actualizacion es exitosa se debe mostrar el estado de exito`() {
        val contrato = ElectronicInvoice(
            id = "1",
            isEnabled = false,
            type = ContractType.LUZ,
            email = "test@iberdrola.es"
        )
        viewModel.selectContract(contrato)

        coEvery { updateUseCase(any()) } returns Unit

        viewModel.performUpdate()

        assertTrue("isSuccess debería ser true", viewModel.state.isSuccess)
        assertEquals(null, viewModel.state.error)
        assertFalse("isLoading debería ser false al terminar", viewModel.state.isLoading)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(testDispatcher)
            try {
                base.evaluate()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
}