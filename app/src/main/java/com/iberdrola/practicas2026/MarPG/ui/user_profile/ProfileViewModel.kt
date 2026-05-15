package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.VerifyUserPasswordUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ProfileValidationResult
import com.iberdrola.practicas2026.MarPG.domain.use_case.users.ValidateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val validateProfile: ValidateProfileUseCase,
    private val verifyUserPasswordUseCase: VerifyUserPasswordUseCase
) : ViewModel() {

    var state by mutableStateOf(ProfileState(isLoading = true))
        private set

    init {
        loadSavedProfile()
        logAnalyticsUseCase("view_perfil_usuario", priority = AnalyticsPriority.HIGH)
    }

    private fun loadSavedProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(800)
            val savedState = userPrefs.userProfileFlow.first()
            state = savedState.copy(
                confirmPassword = savedState.password,
                isSaved = true, 
                isLoading = false, 
                isEditMode = false, 
                isSaveClicked = false, 
                isVerifying = false
            )
        }
    }

    fun onEditClick() {
        if (state.showLogoutDialog || state.isLogoutClicked || state.isLoading || state.isSaving) return
        
        logAnalyticsUseCase("click_editar_perfil", priority = AnalyticsPriority.MEDIUM)
        state = state.copy(isEditClicked = true)
        if (state.password.isNotEmpty()) {
            state = state.copy(
                showSecurityDialog = true,
                securityPasswordInput = "",
                securityPasswordError = null
            )
        } else {
            state = state.copy(isEditMode = true)
        }
    }

    fun onSecurityPasswordChanged(input: String) {
        state = state.copy(securityPasswordInput = input, securityPasswordError = null)
    }

    fun onToggleSecurityPasswordVisibility() {
        state = state.copy(isSecurityPasswordVisible = !state.isSecurityPasswordVisible)
    }

    fun onSecurityConfirmClick() {
        viewModelScope.launch {
            if (verifyUserPasswordUseCase(state.securityPasswordInput)) {
                logAnalyticsUseCase("verificacion_seguridad_correcta", priority = AnalyticsPriority.HIGH)
                state = state.copy(
                    showSecurityDialog = false,
                    isVerifying = true
                )
                delay(1500)
                state = state.copy(
                    isVerifying = false,
                    isEditMode = true,
                    securityPasswordInput = "",
                    isEditClicked = false
                )
            } else {
                logAnalyticsUseCase("error_verificacion_seguridad", mapOf("motivo" to "contraseña incorrecta"), priority = AnalyticsPriority.HIGH)
                state = state.copy(securityPasswordError = R.string.error_incorrect_password)
            }
        }
    }

    fun onSecurityDismiss() {
        logAnalyticsUseCase("click_cancelar_seguridad", priority = AnalyticsPriority.LOW)
        state = state.copy(
            showSecurityDialog = false,
            securityPasswordInput = "",
            securityPasswordError = null,
            isEditClicked = false
        )
    }

    fun onDiscardClick() {
        logAnalyticsUseCase("click_descartar_cambios_perfil", priority = AnalyticsPriority.MEDIUM)
        loadSavedProfile()
        state = state.copy(isEditClicked = false)
    }

    fun onNameChange(newName: String) {
        state = state.copy(name = newName, nameError = null, isSaved = false)
    }

    fun onEmailChange(newEmail: String) {
        state = state.copy(email = newEmail, emailError = null, isSaved = false)
    }

    fun onPhoneChange(newPhone: String) {
        state = state.copy(phone = newPhone, phoneError = null, isSaved = false)
    }

    fun onAddressChanged(newAddress: String) {
        state = state.copy(address = newAddress, isSaved = false)
    }

    fun onPasswordChanged(newPassword: String) {
        state = state.copy(password = newPassword, passwordError = null, isSaved = false)
    }

    fun onConfirmPasswordChanged(newPassword: String) {
        state = state.copy(confirmPassword = newPassword, confirmPasswordError = null, isSaved = false)
    }

    fun saveChanges(onSuccess: () -> Unit) {
        logAnalyticsUseCase("click_guardar_perfil", priority = AnalyticsPriority.MEDIUM)

        val validationResult = validateProfile(
            name = state.name,
            email = state.email,
            phone = state.phone,
            password = state.password,
            confirmPassword = state.confirmPassword
        )

        when (validationResult) {
            is ProfileValidationResult.Success -> {
                state = state.copy(
                    nameError = null,
                    emailError = null,
                    phoneError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    isSaveClicked = true
                )
                viewModelScope.launch {
                    state = state.copy(isSaving = true)
                    delay(2000)
                    userPrefs.updateProfile(state)
                    state = state.copy(
                        isSaving = false,
                        isSaved = true,
                        saveJustFinished = true
                    )
                    onSuccess()
                    delay(2000)
                    state = state.copy(
                        saveJustFinished = false,
                        isEditMode = false,
                        isSaveClicked = false
                    )
                    logAnalyticsUseCase("exito_guardado_perfil", priority = AnalyticsPriority.HIGH)
                }
            }
            is ProfileValidationResult.Error -> {
                state = state.copy(
                    nameError = validationResult.nameError,
                    emailError = validationResult.emailError,
                    phoneError = validationResult.phoneError,
                    passwordError = validationResult.passwordError,
                    confirmPasswordError = validationResult.confirmPasswordError
                )
                logAnalyticsUseCase("error_validacion_perfil", mapOf(
                    "error_nombre" to (validationResult.nameError != null),
                    "error_email" to (validationResult.emailError != null),
                    "error_password" to (validationResult.passwordError != null),
                    "error_confirmacion" to (validationResult.confirmPasswordError != null),
                    "error_telefono" to (validationResult.phoneError != null)
                ), priority = AnalyticsPriority.MEDIUM)
            }
        }
    }

    fun onLogoutClick() {
        if (state.showSecurityDialog || state.isEditMode || state.isEditClicked || state.isLoading || state.isSaving) return

        logAnalyticsUseCase("click_cerrar_sesion", priority = AnalyticsPriority.MEDIUM)
        state = state.copy(
            showLogoutDialog = true,
            isLogoutClicked = true
        )
    }

    fun onDismissLogoutDialog() {
        logAnalyticsUseCase("click_cancelar_cierre_sesion", priority = AnalyticsPriority.LOW)
        state = state.copy(
            showLogoutDialog = false,
            isLogoutClicked = false)
    }

    fun logout(onSuccess: () -> Unit) {
        logAnalyticsUseCase("confirmacion_cierre_sesion", priority = AnalyticsPriority.HIGH)
        viewModelScope.launch {
            userPrefs.setLoggedIn(false)
            userPrefs.clearProfile()
            state = ProfileState()
            onSuccess()
        }
    }
}
