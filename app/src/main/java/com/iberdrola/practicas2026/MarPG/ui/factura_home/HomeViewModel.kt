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
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
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
        logAnalyticsUseCase("view_home", priority = AnalyticsPriority.HIGH)
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

        val cachedValue = remoteConfig.getBoolean("show_gas_contracts")
        state = state.copy(isGasEnabled = cachedValue)

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    val isEnabled = remoteConfig.getBoolean("show_gas_contracts")
                    state = state.copy(isGasEnabled = isEnabled)
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        })

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isGasEnabled = remoteConfig.getBoolean("show_gas_contracts")
                logAnalyticsUseCase("remote_config_fetched", mapOf("enseñar_gas" to isGasEnabled), priority = AnalyticsPriority.LOW)
                state = state.copy(isGasEnabled = isGasEnabled)
            } else {
                state = state.copy(isGasEnabled = true)
            }
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                val isProfileComplete = profile.name.isNotEmpty() &&
                        profile.email.isNotEmpty() &&
                        profile.password.isNotEmpty()

                state = state.copy(
                    userName = profile.name,
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
                        logAnalyticsUseCase("view_feedback_sheet", priority = AnalyticsPriority.LOW)
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

        logAnalyticsUseCase("click_feedback_opcion", mapOf("puntuacion" to target), priority = AnalyticsPriority.HIGH)
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
        logAnalyticsUseCase("click_feedback_no_preguntar_otra", priority = AnalyticsPriority.MEDIUM)
        viewModelScope.launch {
            checkFeedbackUseCase.dontAskAgain()
            state = state.copy(isSheetVisible = false)
        }
    }

    fun onNavigateWithProfileCheck(onSuccess: () -> Unit) {
        onSuccess()
    }
}
