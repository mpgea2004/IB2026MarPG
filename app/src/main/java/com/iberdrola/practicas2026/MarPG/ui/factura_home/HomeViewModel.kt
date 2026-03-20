package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.feedback.CheckFeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Gestión de estado para la Home y control de lógica de feedback con DataStore */@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    /** Controla la visibilidad del BottomSheet de feedback */
    var isSheetVisible by mutableStateOf(false)
        private set

    var userName by mutableStateOf("")
        private set

    init {
        observeFeedback()
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                userName = profile.name.ifEmpty { "Usuario" }
            }
        }
    }
    /** * Se suscribe al flujo de DataStore.
     * Cuando el contador llega a 0 en disco, la UI reacciona automáticamente.
     */
    private fun observeFeedback() {
        viewModelScope.launch {
            checkFeedbackUseCase.shouldShowFeedback().collect { shouldShow ->
                isSheetVisible = shouldShow
            }
        }
    }

    /** * Aplica las reglas de Iberdrola según la opción elegida:
     * - Valorar: 10
     * - Luego: 3
     * - Cerrar: 1
     */
    fun onOptionSelected(target: Int) {
        viewModelScope.launch {
            checkFeedbackUseCase.setNextTregua(target)
        }
    }
}