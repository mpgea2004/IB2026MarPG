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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val checkFeedbackUseCase: CheckFeedbackUseCase,
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        observeFeedback()
        observeUserProfile()
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
                state = state.copy(isSheetVisible = shouldShow)
            }
        }
    }

    fun onOptionSelected(target: Int) {
        viewModelScope.launch {
            checkFeedbackUseCase.setNextTregua(target)
            state = state.copy(isSheetVisible = false)
        }
    }

    fun onDontAskAgain() {
        viewModelScope.launch {
            checkFeedbackUseCase.dontAskAgain()
            state = state.copy(isSheetVisible = false)
        }
    }
}