package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.UpdateElectronicInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ElectronicInvoiceViewModel @Inject constructor(
    private val updateUseCase: UpdateElectronicInvoiceUseCase,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(ElectronicInvoiceState())
        private set

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")
    
    private var hasAcknowledgedSameEmail = false
    private var currentSimulationId = 0L

    init {
        observeUserProfile()
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
            simulatedOtpCode = "",
            resendAttempts = 2
        )
        hasAcknowledgedSameEmail = false
    }

    fun onEmailChanged(nuevoEmail: String) {
        state = state.copy(
            emailInput = nuevoEmail,
            showSameEmailWarning = false
        )
        hasAcknowledgedSameEmail = false
    }

    fun onLegalAccepted(accepted: Boolean) {
        state = state.copy(isLegalAccepted = accepted)
    }

    fun canContinue(): Boolean {
        val email = state.emailInput.trim()
        val isEmailValid = emailPattern.matches(email)
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
            performUpdate()
        } else {
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
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error_unexpected)
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
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = R.string.error_unexpected)
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
            showSimulatedNotification = false
        )
    }

    fun startOtpSimulation() {
        currentSimulationId++
        val simulationId = currentSimulationId

        viewModelScope.launch {
            state = state.copy(otpInput = "", showSimulatedNotification = false)
            delay(1000) 
            
            if (simulationId != currentSimulationId) return@launch

            val simulatedCode = if (state.simulatedOtpCode.isNotEmpty()) {
                state.simulatedOtpCode
            } else {
                val newCode = (100000..999999).random().toString()
                state = state.copy(simulatedOtpCode = newCode)
                newCode
            }
            
            state = state.copy(
                showSimulatedNotification = true,
                simulatedNotificationMessage = "Iberdrola: Su código es $simulatedCode. No lo comparta.",
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

    fun onResendOtp() {
        if (state.resendAttempts > 0) {
            viewModelScope.launch {
                currentSimulationId++
                state = state.copy(
                    resendAttempts = state.resendAttempts - 1,
                    isLoading = true,
                    showResendSuccess = false,
                    otpInput = "",
                    simulatedOtpCode = ""
                )
                delay(2000)
                state = state.copy(isLoading = false, showResendSuccess = true)
                startOtpSimulation()
            }
        }
    }

    fun closeResendBanner() {
        state = state.copy(showResendSuccess = false)
    }

    fun closeSimulatedNotification() {
        state = state.copy(showSimulatedNotification = false)
    }

    fun onShowLegalDetail(title: String, content: String) {
        state = state.copy(
            selectedLegalTitle = title,
            selectedLegalContent = content,
            showLegalSheet = true
        )
    }

    fun onDismissLegalSheet() {
        state = state.copy(showLegalSheet = false)
    }

    fun onContinueClick(onNavigateToOtp: () -> Unit) {
        val isSameEmail = state.emailInput == state.selectedContract?.email
        
        if (isSameEmail && !hasAcknowledgedSameEmail) {
            state = state.copy(showSameEmailWarning = true)
            hasAcknowledgedSameEmail = true
            return
        }

        if (state.userProfile.phone.isEmpty()) {
            state = state.copy(showNoPhoneDialog = true)
        } else {
            state = state.copy(showSameEmailWarning = false)
            onNavigateToOtp()
        }
    }

    fun dismissSameEmailWarning() {
        state = state.copy(showSameEmailWarning = false)
    }

    fun onNewPhoneChanged(nuevoTelefono: String) {
        if (nuevoTelefono.all { it.isDigit() } && nuevoTelefono.length <= 9) {
            state = state.copy(newPhoneInput = nuevoTelefono)
        }
    }

    fun onPasswordChanged(nuevaPass: String) {
        state = state.copy(passwordInput = nuevaPass)
    }

    fun togglePasswordVisibility() {
        state = state.copy(isPasswordVisible = !state.isPasswordVisible)
    }

    fun savePhoneAndContinue(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                delay(1000)

                val userSavedPassword = state.userProfile.password

                val isPasswordCorrect = if (userSavedPassword.isEmpty()) {
                    state.passwordInput == "1234"
                } else {
                    state.passwordInput == userSavedPassword
                }

                if (isPasswordCorrect) {
                    userPrefs.updatePhone(state.newPhoneInput)

                    state = state.copy(
                        showNoPhoneDialog = false,
                        passwordInput = "",
                        error = null
                    )
                    onSuccess()
                } else {
                    state = state.copy(
                        error = R.string.error_incorrect_password
                    )
                }
            } catch (e: Exception) {
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
                
                val userSavedPassword = state.userProfile.password
                val isPasswordCorrect = if (userSavedPassword.isEmpty()) {
                    state.passwordInput == "1234"
                } else {
                    state.passwordInput == userSavedPassword
                }

                if (isPasswordCorrect) {
                    userPrefs.updateAddress(address)
                    state = state.copy(showNoAddressDialog = false, passwordInput = "")
                    performDeactivate()
                } else {
                    state = state.copy(error = R.string.error_incorrect_password)
                }
            } catch (e: Exception) {
                state = state.copy(error = R.string.error_unexpected)
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }
    
    fun onNewAddressChanged(newAddress: String) {
        state = state.copy(newAddressInput = newAddress)
    }

    fun closeAddressDialog() {
        state = state.copy(showNoAddressDialog = false, error = null, passwordInput = "", newAddressInput = "")
    }

    fun closeDeactivationDialog() {
        state = state.copy(showDeactivationConfirmDialog = false)
    }

    fun discardChanges() {
        state.selectedContract?.let { selectContract(it) }
    }
}
