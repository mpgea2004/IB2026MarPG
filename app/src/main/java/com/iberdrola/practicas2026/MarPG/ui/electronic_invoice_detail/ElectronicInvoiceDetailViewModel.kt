package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.FormatUserPhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateUserPhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.VerifyUserPasswordUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ElectronicInvoiceViewModel @Inject constructor(
    private val updateUseCase: UpdateElectronicInvoiceUseCase,
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val verifyUserPasswordUseCase: VerifyUserPasswordUseCase,
    private val updateUserPhoneUseCase: UpdateUserPhoneUseCase,
    private val formatUserPhoneUseCase: FormatUserPhoneUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    var state by mutableStateOf(ElectronicInvoiceState())
        private set

    val phoneToShow: String
        get() = formatUserPhoneUseCase(state.userProfile.phone)

    val events = ElectronicInvoiceEvents(
        onSelectContract = { contract ->
            selectContract(contract)
        },

        onEmailChange = { nuevoEmail ->
            state = state.copy(
                emailInput = nuevoEmail,
                isNextEnabled = canContinue()
            )
        },

        onLegalCheckChange = { accepted ->
            state = state.copy(
                isLegalAccepted = accepted,
                isNextEnabled = canContinue()
            )
            logAnalytics("efactura_legal_toggle", mapOf("accepted" to accepted))
        },

        onOtpChange = { nuevoOtp ->
            if (nuevoOtp.length <= 6 && nuevoOtp.all { it.isDigit() }) {
                state = state.copy(
                    otpInput = nuevoOtp,
                    isNextEnabled = nuevoOtp.length == 6
                )
            }
        },

        onConfirmUpdate = {
            logAnalytics("efactura_confirm_update_click")
            performUpdate()
        },

        onConfirmDeactivate = {
            logAnalytics("efactura_confirm_deactivate_click")
            performDeactivate()
        },

        onResendOtp = {
            handleResendOtp()
        },

        onCloseBanner = {
            state = state.copy(showResendSuccess = false)
            logAnalytics("efactura_close_success_banner")
        },

        onShowLegal = { title, content ->
            state = state.copy(selectedLegalTitle = title, selectedLegalContent = content, showLegalSheet = true)
            logAnalytics("efactura_view_legal_detail", mapOf("section" to title))
        },

        onDismissLegal = {
            state = state.copy(showLegalSheet = false)
        },

        onBack = {
            logAnalytics("efactura_nav_back", mapOf("from_step" to state.currentStep.name))
        },
        onViewScreen = { screenName ->
            logAnalytics("view_screen", mapOf("screen_name" to screenName))
        },
    )

    init {
        observeUserProfile()
        logAnalytics("view_efactura_flow_start")
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                state = state.copy(
                    userProfile = profile
                )

                if (state.emailInput.isEmpty() && profile.email.isNotEmpty()) {
                    state = state.copy(emailInput = profile.email)
                }
            }
        }
    }

    fun selectContract(contract: ElectronicInvoice) {
        state = state.copy(
            selectedContract = contract,
            emailInput = contract.email ?: "",
            isEditingEmail = contract.isEnabled,
            isLegalAccepted = false,
            isSuccess = false,
            error = null
        )
        logAnalytics("efactura_contract_selected", mapOf(
            "contract_type" to contract.type.toString(),
            "already_enabled" to contract.isEnabled
        ))
    }

    fun canContinue(): Boolean {
        val email = state.emailInput.trim()
        val isEmailValid = validateEmailUseCase(email)
        val contract = state.selectedContract ?: return false

        return if (!contract.isEnabled) {
            isEmailValid && state.isLegalAccepted
        } else {
            isEmailValid
        }
    }

    fun performUpdate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val updatedContract = contract.copy(
                    isEnabled = true,
                    email = state.emailInput
                )

                updateUseCase(updatedContract)

                state = state.copy(
                    selectedContract = updatedContract,
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
                logAnalytics("efactura_update_success", mapOf(
                    "type" to contract.type.toString(),
                    "step" to "final_confirmation"
                ))
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
                logAnalytics("efactura_api_error", mapOf("error_msg" to (e.message ?: "unknown")))
            }
        }
    }

    fun performDeactivate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true, isSuccess = false)

            try {
                val updatedContract = contract.copy(
                    isEnabled = false
                )

                updateUseCase(updatedContract)

                state = state.copy(
                    selectedContract = updatedContract,
                    isDeactivation = true,
                    isLoading = false,
                    isSuccess = true,
                    error = null
                )
                logAnalytics("efactura_deactivate_success")
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
                logAnalytics("efactura_deactivate_error", mapOf("reason" to e.message))
            }
        }
    }

    fun handleResendOtp() {
        if (state.resendAttempts > 0) {
            viewModelScope.launch {
                state = state.copy(
                    resendAttempts = state.resendAttempts - 1,
                    isLoading = true,
                    showResendSuccess = false
                )
                delay(1500)
                state = state.copy(isLoading = false, showResendSuccess = true)
                logAnalytics("efactura_otp_resend_click", mapOf("attempts_remaining" to state.resendAttempts))
            }
        }else {
            logAnalytics("efactura_otp_resend_limit_reached")
        }
    }
    fun onContinueClick(onNavigateToOtp: () -> Unit) {
        if (state.userProfile.phone.isEmpty()) {
            state = state.copy(showNoPhoneDialog = true)
            logAnalytics("efactura_missing_phone_alert")
        } else {
            onNavigateToOtp()
        }
    }

    fun onNewPhoneChanged(nuevoTelefono: String) {
        if (nuevoTelefono.all { it.isDigit() } && nuevoTelefono.length <= 9) {
            state = state.copy(newPhoneInput = nuevoTelefono)
        }
    }

    fun onPasswordChanged(nuevaPass: String) {
        state = state.copy(passwordInput = nuevaPass)
    }

    fun savePhoneAndContinue(onSuccess: () -> Unit) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            // 1. Validamos contraseña mediante el caso de uso
            if (verifyUserPasswordUseCase(state.passwordInput)) {

                // 2. Intentamos actualizar
                val success = updateUserPhoneUseCase(state.newPhoneInput)

                if (success) {
                    state = state.copy(showNoPhoneDialog = false, passwordInput = "")
                    logAnalytics("efactura_phone_update_success")
                    onSuccess()
                } else {
                    state = state.copy(error = "El formato del teléfono no es válido")
                }
            } else {
                state = state.copy(error = "La contraseña introducida no es correcta")
            }
            state = state.copy(isLoading = false)
        }
    }

    fun closePhoneDialog() {
        state = state.copy(showNoPhoneDialog = false, error = null, passwordInput = "")
    }

    fun logSuccessScreen(isDeactivation: Boolean, isEditingEmail: Boolean, contractType: String) {
        val eventName = when {
            isDeactivation -> "elec_invoice_deactivation_success"
            isEditingEmail -> "elec_invoice_modification_success"
            else -> "elec_invoice_activation_success"
        }
        logAnalytics(eventName, mapOf("contract_type" to contractType))
        logAnalytics("view_screen", mapOf("screen_name" to "Pantalla_Exito_Final_Verde"))
    }

    fun logDetailScreenView(contractType: String) {
        logAnalytics("view_screen", mapOf(
            "screen_name" to "Detalle_Factura_Electronica_Mar",
            "contract_type" to contractType
        ))
    }

    fun logDeactivateAttempt(contractType: String) {
        logAnalytics("elec_invoice_deactivate_attempt", mapOf("contract_type" to contractType))
    }

    fun logDeactivateCancel(contractType: String) {
        logAnalytics("elec_invoice_deactivation_cancel", mapOf("contract_type" to contractType))
    }

    fun logDeactivateConfirmed(contractId: String) {
        logAnalytics("elec_invoice_deactivation_confirmed", mapOf("contract_id" to contractId))
    }

    fun logAnalytics(name: String, params: Map<String, Any?> = emptyMap()) {
        logAnalyticsUseCase(name, params)
    }
}