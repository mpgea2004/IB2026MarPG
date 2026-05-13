package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        logAnalyticsUseCase("view_home")
        fetchRemoteConfig()
        observeFeedback()
        observeUserProfile()
    }

    private fun fetchRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        
        remoteConfig.setDefaultsAsync(mapOf("show_gas_contracts" to true))

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    val isEnabled = remoteConfig.getBoolean("show_gas_contracts")
                    state = state.copy(isGasEnabled = isEnabled)
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        })

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val isGasEnabled = remoteConfig.getBoolean("show_gas_contracts")
            logAnalyticsUseCase("remote_config_fetched", mapOf("enseñar_gas" to isGasEnabled))
            state = state.copy(isGasEnabled = isGasEnabled)
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                val isProfileComplete = profile.name.isNotEmpty() &&
                        profile.email.isNotEmpty() &&
                        profile.password.isNotEmpty()

                state = state.copy(
                    userName = profile.name.ifEmpty { "Usuario" },
                    isProfileComplete = isProfileComplete,
                    isFullProfileComplete = isProfileComplete &&
                            profile.phone.isNotEmpty() &&
                            profile.address.isNotEmpty()
                )
            }
        }
    }
    
    private fun observeFeedback() {
        viewModelScope.launch {
            checkFeedbackUseCase.shouldShowFeedback().collect { shouldShow ->
                if (shouldShow) {
                    if (!state.isSheetVisible) {
                        logAnalyticsUseCase("view_feedback_sheet")
                        state = state.copy(
                            isFeedbackSubmitted = false,
                            isSheetVisible = true
                        )
                    }
                } else {
                    if (!state.isFeedbackSubmitted) {
                        state = state.copy(isSheetVisible = false)
                    }
                }
            }
        }
    }

    fun onOptionSelected(target: Int) {
        if (state.isFeedbackSubmitted && target == 1) {
            state = state.copy(isSheetVisible = false)
            return 
        }

        logAnalyticsUseCase("click_feedback_opcion", mapOf("puntuacion" to target))
        viewModelScope.launch {
            if (target == 10) {
                state = state.copy(isFeedbackSubmitted = true)
                checkFeedbackUseCase.setNextTregua(target)
            } else {
                checkFeedbackUseCase.setNextTregua(target)
                state = state.copy(isSheetVisible = false)
            }
        }
    }

    fun onCloseSheet() {
        state = state.copy(isSheetVisible = false)
    }

    fun onDontAskAgain() {
        logAnalyticsUseCase("click_feedback_no_preguntar_otra")
        viewModelScope.launch {
            checkFeedbackUseCase.dontAskAgain()
            state = state.copy(isSheetVisible = false)
        }
    }

    fun onNavigateWithProfileCheck(onSuccess: () -> Unit) {
        if (state.isProfileComplete || state.userName == "Invitado") {
            onSuccess()
        } else {
            state = state.copy(
                showGuestDialog = true,
                pendingNavigation = onSuccess
            )
        }
    }

    fun onConfirmGuest() {
        logAnalyticsUseCase("click_confirmar_invitado")
        viewModelScope.launch {
            val guestProfile = ProfileState(
                name = "Invitado",
                password = "1234",
                confirmPassword = "1234",
                email = "",
                phone = "",
                address = ""
            )
            userPrefs.updateProfile(guestProfile)
            state = state.copy(showGuestDialog = false)
            state.pendingNavigation?.invoke()
            state = state.copy(pendingNavigation = null)
        }
    }

    fun onDismissGuestDialog() {
        logAnalyticsUseCase("click_cancelar_invitado")
        state = state.copy(
            showGuestDialog = false,
            pendingNavigation = null
        )
    }
}
