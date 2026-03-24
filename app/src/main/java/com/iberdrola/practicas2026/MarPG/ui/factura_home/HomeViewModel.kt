package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Gestión de estado para la Home y control de lógica de feedback con DataStore */@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    val events = HomeEvents(
        onProfileClick = {
            logAnalyticsUseCase("click_home_profile")
        },
        onNavigateToInvoices = {
            logAnalyticsUseCase("nav_ver_facturas", mapOf("desde" to "home"))
        },
        onNavigateToElectronicInvoice = {
            logAnalyticsUseCase("nav_factura_electronica", mapOf("desde" to "home"))
        },
        onToggleCloud = { enabled ->
            state = state.copy(isCloudEnabled = enabled)
            logAnalyticsUseCase("config_data_source", mapOf("modo" to if (enabled) "nube" else "local"))
        },
        onFeedbackOption = { target ->
            val optionName = when(target) {
                10 -> "valorar"
                3 -> "luego"
                else -> "cerrar"
            }
            logAnalyticsUseCase("click_feedback_option", mapOf("option" to optionName))
            viewModelScope.launch {
                checkFeedbackUseCase.setNextTregua(target)
            }
        },
        onDismissFeedback = {
            viewModelScope.launch { checkFeedbackUseCase.setNextTregua(1) }
        }
    )

    init {
        logAnalyticsUseCase("view_home_mar")
        observeFeedback()
        observeUserProfile()
    }


    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                state = state.copy(userName = profile.name.ifEmpty { "Usuario" })            }
        }
    }
    private fun observeFeedback() {
        viewModelScope.launch {
            checkFeedbackUseCase.shouldShowFeedback().collect { shouldShow ->
                state = state.copy(isSheetVisible = shouldShow)
            }
        }
    }
}