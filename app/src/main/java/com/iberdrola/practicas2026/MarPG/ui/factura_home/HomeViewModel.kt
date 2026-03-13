package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.UpdateFeedbackConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val updateFeedbackUseCase: UpdateFeedbackConfigUseCase
) : ViewModel() {

    var isSheetVisible by mutableStateOf(false)

    //Función que llamo desde el HomeScreen cada vez que se muestre
    fun checkPendingFeedback() {
        if (feedbackRepository.feedbackCount >= feedbackRepository.feedbackTarget) {
            isSheetVisible = true
        }
    }

    fun onOptionSelected(target: Int) {
        updateFeedbackUseCase(target) //Esto resetea el contador a 0 y cambia el target
        isSheetVisible = false
    }
}