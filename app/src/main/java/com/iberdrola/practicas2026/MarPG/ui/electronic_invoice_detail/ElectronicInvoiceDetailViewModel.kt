package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.FormatUserPhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateUserPhoneUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.VerifyUserPasswordUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
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

    private var hasAcknowledgedSameEmail = false
    private var currentSimulationId = 0L

    companion object {
        private const val TWENTY_FOUR_HOURS_MS = 86400000L
        private const val MAX_RESEND_ATTEMPTS = 3
    }

    init {
        observeUserProfile()
        logAnalytics("view_electronic_invoice_flow_start", mapOf("message" to "Inicio del flujo de factura electrónica"))
        observeOtpAttempts()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                state = state.copy(
                    userProfile = profile,
                    formattedPhone = formatUserPhoneUseCase(profile.phone)
                )

                if (state.emailInput.isEmpty() && profile.email.isNotEmpty()) {
                    state = state.copy(emailInput = profile.email)
                }
            }
        }
    }

    private fun observeOtpAttempts() {
        viewModelScope.launch {
            snapshotFlow { state.selectedContract?.type }.collectLatest { type ->
                if (type == null) {
                    state = state.copy(resendAttempts = MAX_RESEND_ATTEMPTS, lastResendTimestamp = 0L, remainingTime = "")
                } else {
                    userPrefs.getOtpResendDataFlow(type).collectLatest { (attempts, timestamp) ->
                        while (true) {
                            val currentTime = System.currentTimeMillis()
                            val timeElapsed = currentTime - timestamp
                            val isExpired = timestamp != 0L && (timeElapsed > TWENTY_FOUR_HOURS_MS)

                            val finalAttempts = if (isExpired) MAX_RESEND_ATTEMPTS else attempts
                            val finalTimestamp = if (isExpired) 0L else timestamp

                            val timeRemainingMs = (finalTimestamp + TWENTY_FOUR_HOURS_MS) - currentTime

                            val formattedTime = if (finalTimestamp != 0L && finalAttempts <= 0 && timeRemainingMs > 0) {
                                val totalSeconds = timeRemainingMs / 1000
                                val hours = totalSeconds / 3600
                                val minutes = (totalSeconds % 3600) / 60
                                val seconds = totalSeconds % 60
                                if (hours > 0) {
                                    String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                                } else {
                                    String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                                }
                            } else ""

                            state = state.copy(
                                resendAttempts = finalAttempts,
                                lastResendTimestamp = finalTimestamp,
                                remainingTime = formattedTime
                            )

                            if (isExpired || finalAttempts > 0 || timestamp == 0L) break

                            delay(1000L)
                        }
                    }
                }
            }
        }
    }

    fun selectContract(contract: ElectronicInvoice) {
        currentSimulationId++
        state = state.copy(
            selectedContract = contract,
            emailInput = contract.email ?: "",
            isEditingEmail = contract.isEnabled,
            isLegalAccepted = false,
            isSuccess = false,
            error = null,
            showSameEmailWarning = false,
            otpInput = "",
            showSimulatedNotification = false,
            simulatedOtpCode = ""
        )
        hasAcknowledgedSameEmail = false
        val typeName = if (contract.type == ContractType.LUZ) "Luz" else "Gas"
        logAnalytics("electronic_invoice_contract_selected", mapOf(
            "contract_type" to typeName,
            "already_enabled" to contract.isEnabled,
            "message" to "Contrato seleccionado"
        ))
    }

    fun onEmailChanged(nuevoEmail: String) {
        state = state.copy(
            emailInput = nuevoEmail,
            showSameEmailWarning = false,
            error = null
        )
        hasAcknowledgedSameEmail = false
    }

    fun onLegalAccepted(accepted: Boolean) {
        state = state.copy(isLegalAccepted = accepted)
    }

    fun canContinue(): Boolean {
        val email = state.emailInput.trim()
        val isEmailValid = validateEmailUseCase(email)
        val contract = state.selectedContract ?: return false

        val result = if (!contract.isEnabled) {
            isEmailValid && state.isLegalAccepted
        } else {
            isEmailValid
        }

        return result
    }

    fun verifyOtpAndPerformUpdate() {
        if (state.otpInput == state.simulatedOtpCode) {
            logAnalytics("otp_verification_success", mapOf("message" to "Verificación de OTP correcta"))
            val type = state.selectedContract?.type
            viewModelScope.launch {
                if (type != null) {
                    userPrefs.updateOtpResendData(type, MAX_RESEND_ATTEMPTS, 0L)
                }
            }
            performUpdate()
        } else {
            logAnalytics("otp_verification_error", mapOf("reason" to "Código incorrecto"))
            state = state.copy(error = R.string.error_incorrect_otp)
        }
    }

    private fun performUpdate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                delay(2500)

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
                val typeName = if (contract.type == ContractType.LUZ) "Luz" else "Gas"
                logAnalytics("electronic_invoice_update_success", mapOf(
                    "type" to typeName,
                    "step" to "confirmacion_final",
                    "message" to "Factura electrónica actualizada con éxito"
                ))
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error_unexpected)
                logAnalytics("electronic_invoice_api_error", mapOf("message" to "Error al actualizar factura electrónica"))
            }
        }
    }

    fun performDeactivate() {
        val contract = state.selectedContract ?: return

        viewModelScope.launch {
            state = state.copy(isLoading = true, isSuccess = false)

            try {
                delay(2000)
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
                logAnalytics("electronic_invoice_deactivate_success", mapOf("message" to "Desactivación de factura electrónica exitosa"))
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error_unexpected)
                logAnalytics("electronic_invoice_deactivate_error", mapOf("reason" to "Error en la desactivación"))
            }
        }
    }

    fun onOtpChanged(nuevoOtp: String) {
        if (nuevoOtp.length <= 6) {
            state = state.copy(otpInput = nuevoOtp, error = null)
        }
    }

    fun clearOtp() {
        currentSimulationId++
        state = state.copy(
            otpInput = "",
            error = null,
            showSimulatedNotification = false,
            simulatedOtpCode = ""
        )
    }

    fun startOtpSimulation(isPermissionGranted: Boolean = true) {
        if (!isPermissionGranted) {
            state = state.copy(showPermissionDialog = true)
            return
        }

        currentSimulationId++
        val simulationId = currentSimulationId

        viewModelScope.launch {
            val isFirstTime = state.simulatedOtpCode.isEmpty()

            state = state.copy(otpInput = "", showSimulatedNotification = false)
            delay(1000)


            if (!isFirstTime) {
                delay(500)
                if (simulationId == currentSimulationId) {
                    state = state.copy(otpInput = state.simulatedOtpCode)
                }
                return@launch
            }

            delay(1000)

            if (simulationId != currentSimulationId) return@launch

            val simulatedCode = (100000..999999).random().toString()
            state = state.copy(simulatedOtpCode = simulatedCode)

            state = state.copy(
                showSimulatedNotification = true,
            )

            delay(2000)

            if (simulationId != currentSimulationId) return@launch

            simulatedCode.forEachIndexed { index, _ ->
                delay(100)
                if (simulationId != currentSimulationId) return@forEachIndexed
                state = state.copy(otpInput = simulatedCode.take(index + 1))
            }

            delay(2000)
            if (simulationId != currentSimulationId) return@launch
            state = state.copy(showSimulatedNotification = false)
        }
    }

    fun onResendOtp(isPermissionGranted: Boolean = true) {
        if (!isPermissionGranted) {
            state = state.copy(showPermissionDialog = true)
            return
        }

        val type = state.selectedContract?.type ?: return
        if (state.resendAttempts > 0) {
            viewModelScope.launch {
                currentSimulationId++
                val newAttempts = state.resendAttempts - 1
                val timestamp = System.currentTimeMillis()

                userPrefs.updateOtpResendData(type, newAttempts, timestamp)

                state = state.copy(
                    isLoading = true,
                    showResendSuccess = false,
                    otpInput = "",
                    simulatedOtpCode = ""
                )
                delay(2000)
                state = state.copy(isLoading = false, showResendSuccess = true)
                startOtpSimulation(isPermissionGranted = true)
                logAnalytics("electronic_invoice_otp_resend_click", mapOf(
                    "attempts_remaining" to state.resendAttempts,
                    "message" to "Reenvío de código OTP solicitado"
                ))
            }
        }else {
            logAnalytics("electronic_invoice_otp_resend_limit_reached", mapOf("message" to "Límite de reenvíos OTP alcanzado"))
        }
    }

    fun closeResendBanner() {
        state = state.copy(showResendSuccess = false)
    }
    

    fun onShowLegalDetail(title: String, content: String) {
        state = state.copy(
            selectedLegalTitle = title,
            selectedLegalContent = content,
            showLegalSheet = true
        )
        logAnalytics("click_show_legal_detail", mapOf(
            "title" to title,
            "message" to "Visualización de detalle legal"
        ))
    }

    fun onDismissLegalSheet() {
        state = state.copy(showLegalSheet = false)
    }

    fun onContinueClick(onNavigateToOtp: () -> Unit) {
        if (state.resendAttempts <= 0 && state.simulatedOtpCode.isEmpty()) {
            logAnalytics("click_continue_no_otp_attempts", mapOf("message" to "Click en continuar sin intentos OTP"))
            state = state.copy(showNoAttemptsDialog = true)
            return
        }

        if (!canContinue()) {
            val email = state.emailInput.trim()
            if (!validateEmailUseCase(email)) {
                logAnalytics("validation_error_email", mapOf(
                    "email" to email,
                    "message" to "Error de validación: email no válido"
                ))
                state = state.copy(error = R.string.error_invalid_email_format)
            }
            return
        }

        val isSameEmail = state.emailInput == state.selectedContract?.email

        if (isSameEmail && !hasAcknowledgedSameEmail) {
            state = state.copy(showSameEmailWarning = true)
            hasAcknowledgedSameEmail = true
            logAnalytics("view_same_email_warning", mapOf("message" to "Aviso de mismo email mostrado"))
            return
        }

        if (state.userProfile.phone.isEmpty()) {
            state = state.copy(showNoPhoneDialog = true)
            logAnalytics("electronic_invoice_missing_phone_alert", mapOf("message" to "Alerta: falta teléfono del usuario"))
        } else {
            state = state.copy(showSameEmailWarning = false)
            state = state.copy(
                currentStep = ElectronicInvoiceStep.VERIFICATION,
                showSameEmailWarning = false
            )
            logAnalytics("click_continue_to_otp", mapOf("message" to "Continuar hacia verificación OTP"))
            onNavigateToOtp()
        }
    }

    fun dismissSameEmailWarning() {
        state = state.copy(showSameEmailWarning = false)
    }

    fun onNewPhoneChanged(nuevoTelefono: String) {
        if (nuevoTelefono.all { it.isDigit() } && nuevoTelefono.length <= 9) {
            state = state.copy(newPhoneInput = nuevoTelefono, error = null)
        }
    }

    fun onPasswordChanged(nuevaPass: String) {
        state = state.copy(passwordInput = nuevaPass, error = null)
    }

    fun togglePasswordVisibility() {
        state = state.copy(isPasswordVisible = !state.isPasswordVisible)
    }

    fun savePhoneAndContinue(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                delay(1000)

                val isPasswordCorrect = verifyUserPasswordUseCase(state.passwordInput)

                if (isPasswordCorrect) {
                    val wasUpdated = updateUserPhoneUseCase(state.newPhoneInput)
                    if (wasUpdated) {
                        logAnalytics("save_phone_success", mapOf("message" to "Teléfono guardado correctamente"))
                        state = state.copy(
                            showNoPhoneDialog = false,
                            passwordInput = "",
                            error = null
                        )
                        onSuccess()
                    } else {
                        logAnalytics("save_phone_error", mapOf("reason" to "Formato de teléfono inválido"))
                        state = state.copy(error = R.string.error_invalid_phone_format)
                    }
                } else {
                    logAnalytics("save_phone_error", mapOf("reason" to "Contraseña incorrecta"))
                    state = state.copy(
                        error = R.string.error_incorrect_password
                    )
                }
            } catch (e: Exception) {
                logAnalytics("save_phone_error", mapOf("reason" to "Error inesperado al guardar teléfono"))
                state = state.copy(error = R.string.error_unexpected)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun closePhoneDialog() {
        state = state.copy(showNoPhoneDialog = false, error = null, passwordInput = "")
    }

    fun onDeactivateClick() {
        logAnalytics("click_deactivate_electronic_invoice", mapOf("message" to "Click en desactivar factura electrónica"))
        if (state.userProfile.address.isEmpty()) {
            state = state.copy(showNoAddressDialog = true)
        } else {
            state = state.copy(showDeactivationConfirmDialog = true)
        }
    }

    fun onDeactivateWithAddress(address: String) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                delay(1000)

                val isPasswordCorrect = verifyUserPasswordUseCase(state.passwordInput)

                if (isPasswordCorrect) {
                    logAnalytics("save_address_and_deactivate_success", mapOf("message" to "Dirección guardada y desactivación solicitada"))
                    userPrefs.updateAddress(address)
                    state = state.copy(showNoAddressDialog = false, passwordInput = "")
                    performDeactivate()
                } else {
                    logAnalytics("save_address_and_deactivate_error", mapOf("reason" to "Contraseña incorrecta"))
                    state = state.copy(error = R.string.error_incorrect_password)
                }
            } catch (e: Exception) {
                logAnalytics("save_address_and_deactivate_error", mapOf("reason" to "Error inesperado al guardar dirección"))
                state = state.copy(error = R.string.error_unexpected)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun onNewAddressChanged(newAddress: String) {
        state = state.copy(newAddressInput = newAddress, error = null)
    }

    fun closeAddressDialog() {
        state = state.copy(showNoAddressDialog = false, error = null, passwordInput = "", newAddressInput = "")
    }

    fun closeDeactivationDialog() {
        state = state.copy(showDeactivationConfirmDialog = false)
    }

    fun discardChanges() {
        logAnalytics("click_discard_electronic_invoice_changes", mapOf("message" to "Cambios de factura electrónica descartados"))
        state.selectedContract?.let { selectContract(it) }
    }

    fun onEditClick(onNavigate: () -> Unit) {
        logAnalytics("click_edit_electronic_invoice", mapOf("message" to "Click en editar factura electrónica"))
        if (state.resendAttempts <= 0 && state.simulatedOtpCode.isEmpty()) {
            state = state.copy(showNoAttemptsDialog = true)
        } else {
            onNavigate()
        }
    }

    fun closeNoAttemptsDialog() {
        state = state.copy(showNoAttemptsDialog = false)
    }

    fun onNavigateStarted() {
        state = state.copy(isNavigating = true)
    }

    fun logSuccessScreen(isDeactivation: Boolean, isEditingEmail: Boolean, contractType: String) {
        val eventName = when {
            isDeactivation -> "electronic_invoice_deactivation_success"
            isEditingEmail -> "electronic_invoice_modification_success"
            else -> "electronic_invoice_activation_success"
        }
        logAnalytics(eventName, mapOf(
            "contract_type" to contractType,
            "message" to "Proceso finalizado con éxito"
        ))
        logAnalytics("view_screen", mapOf("screen_name" to "Pantalla de éxito final"))
    }

    fun logAnalytics(name: String, params: Map<String, Any?> = emptyMap()) {
        logAnalyticsUseCase(name, params)
    }

    fun onNavigateFinished() {
        state = state.copy(isNavigating = false)
    }

    fun updatePermissionPermanentlyDenied(isDenied: Boolean) {
        state = state.copy(
            isPermissionPermanentlyDenied = isDenied,
            showPermissionDialog = true
        )
    }

    fun dismissPermissionDialog() {
        state = state.copy(showPermissionDialog = false)
    }

    fun onPermissionGranted() {
        state = state.copy(showPermissionDialog = false)
        startOtpSimulation(true)
    }

    fun onPermissionNeeded(isPermanentlyDenied: Boolean) {
        state = state.copy(
            showPermissionDialog = true,
            isPermissionPermanentlyDenied = isPermanentlyDenied
        )
    }
}