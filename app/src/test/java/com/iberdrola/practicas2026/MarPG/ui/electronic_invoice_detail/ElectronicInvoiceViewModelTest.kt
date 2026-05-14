package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.*
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var viewModel: ElectronicInvoiceViewModel

    private val updateUseCase = mockk<UpdateElectronicInvoiceUseCase>(relaxed = true)
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val logAnalytics = mockk<LogAnalyticsEventUseCase>(relaxed = true)
    private val verifyPassword = mockk<VerifyUserPasswordUseCase>(relaxed = true)
    private val updatePhone = mockk<UpdateUserPhoneUseCase>(relaxed = true)
    private val formatPhone = mockk<FormatUserPhoneUseCase>(relaxed = true)
    private val validateEmail = mockk<ValidateEmailUseCase>(relaxed = true)

    private val userProfileFlow = MutableStateFlow(ProfileState())
    private val otpResendFlow = MutableStateFlow(Pair(3, 0L))

    @Before
    fun setUp() {
        every { userPrefs.userProfileFlow } returns userProfileFlow
        every { userPrefs.getOtpResendDataFlow(any()) } returns otpResendFlow
        every { formatPhone(any()) } returns "600 000 000"

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

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando se selecciona un contrato se debe inicializar el estado correctamente`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = false, email = "test@test.com")
        viewModel.selectContract(contract)

        assertEquals(contract, viewModel.state.selectedContract)
        assertEquals("test@test.com", viewModel.state.emailInput)
        assertFalse(viewModel.state.isLegalAccepted)
        verify { logAnalytics("electronic_invoice_contract_selected", any(), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `canContinue debe ser verdadero solo si el email es valido y terminos aceptados para activacion`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = false, email = null)
        viewModel.selectContract(contract)
        
        every { validateEmail("test@test.com") } returns true
        viewModel.onEmailChanged("test@test.com")
        
        viewModel.onLegalAccepted(false)
        assertFalse(viewModel.canContinue())
        
        viewModel.onLegalAccepted(true)
        assertTrue(viewModel.canContinue())
    }

    @Test
    fun `verifyOtpAndPerformUpdate debe llamar a performUpdate si el OTP es correcto`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = false, email = null)
        viewModel.selectContract(contract)
        advanceUntilIdle()
        
        viewModel.verifyOtpAndPerformUpdate()

        advanceTimeBy(100)
        assertTrue(viewModel.state.isLoading)
        
        advanceTimeBy(2500)
        
        assertTrue(viewModel.state.isSuccess)
        coVerify { updateUseCase(any()) }
    }

    @Test
    fun `onResendOtp debe decrementar intentos y actualizar el repositorio`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = false, email = null)
        viewModel.selectContract(contract)
        advanceUntilIdle()

        assertEquals(3, viewModel.state.resendAttempts)

        viewModel.onResendOtp(true)
        advanceUntilIdle()

        coVerify { userPrefs.updateOtpResendData(ContractType.LUZ, 2, any()) }
        verify { logAnalytics("electronic_invoice_otp_resend_click", any(), any()) }
    }

    @Test
    fun `onContinueClick debe mostrar cuidado si el email es el mismo y no se le ha avisado ya`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = false, email = "mismo@test.com")
        viewModel.selectContract(contract)
        viewModel.onEmailChanged("mismo@test.com")
        every { validateEmail(any()) } returns true
        viewModel.onLegalAccepted(true)
        userProfileFlow.value = ProfileState(phone = "600000000")
        advanceUntilIdle()

        viewModel.onContinueClick { }

        assertTrue(viewModel.state.showSameEmailWarning)
        verify { logAnalytics("view_same_email_warning", any(), any()) }
    }

    @Test
    fun `savePhoneAndContinue debe validar password y actualizar telefono`() = runTest(testDispatcher) {
        viewModel.onNewPhoneChanged("611223344")
        coEvery { verifyPassword(any()) } returns true
        coEvery { updatePhone("611223344") } returns true

        var successCalled = false
        viewModel.savePhoneAndContinue { successCalled = true }

        advanceUntilIdle()
        assertTrue(successCalled)
        coVerify { updatePhone("611223344") }
        verify { logAnalytics("save_phone_success", any(), any()) }
    }

    @Test
    fun `performDeactivate debe actualizar el contrato y mostrar exito despues del delay`() = runTest(testDispatcher) {
        val contract = ElectronicInvoice("1", type = ContractType.LUZ, isEnabled = true, email = "test@test.com")
        viewModel.selectContract(contract)
        advanceUntilIdle()

        viewModel.performDeactivate()

        advanceTimeBy(100)
        assertTrue(viewModel.state.isLoading)
        
        advanceTimeBy(2000)
        assertTrue(viewModel.state.isSuccess)
        assertTrue(viewModel.state.isDeactivation)
        assertFalse(viewModel.state.selectedContract!!.isEnabled)
        verify { logAnalytics("electronic_invoice_deactivate_success", any(), any()) }
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
