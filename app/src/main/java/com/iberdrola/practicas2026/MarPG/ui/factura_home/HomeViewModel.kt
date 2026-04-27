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

    var isSheetVisible by mutableStateOf(false)
        private set

    var userName by mutableStateOf("")
        private set

    var isProfileComplete by mutableStateOf(false)
        private set

    var isFullProfileComplete by mutableStateOf(false)
        private set

    init {
        observeFeedback()
        observeUserProfile()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            userPrefs.userProfileFlow.collect { profile ->
                userName = profile.name.ifEmpty { "Usuario" }
                isProfileComplete = profile.name.isNotEmpty() &&
                        profile.email.isNotEmpty() &&
                        profile.password.isNotEmpty()
                isFullProfileComplete = isProfileComplete &&
                        profile.phone.isNotEmpty() &&
                        profile.address.isNotEmpty()
            }
        }
    }
    private fun observeFeedback() {
        viewModelScope.launch {
            checkFeedbackUseCase.shouldShowFeedback().collect { shouldShow ->
                isSheetVisible = shouldShow
            }
        }
    }
    fun onOptionSelected(target: Int) {
        viewModelScope.launch {
            checkFeedbackUseCase.setNextTregua(target)
        }
    }
}