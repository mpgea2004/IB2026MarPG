package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import com.iberdrola.practicas2026.MarPG.domain.use_case.UpdateFeedbackConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Gestión de estado para la Home y control de lógica de feedback */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val updateFeedbackUseCase: UpdateFeedbackConfigUseCase
) : ViewModel() {

    /** Controla la visibilidad del BottomSheet de feedback */
    var isSheetVisible by mutableStateOf(false)

    /** Verifica si el contador ha alcanzado el objetivo para mostrar el feedback */
    fun checkPendingFeedback() {
        if (feedbackRepository.feedbackCount >= feedbackRepository.feedbackTarget) {
            isSheetVisible = true
        }
    }

    /** * Actualiza la configuración de tregua y oculta el diálogo
     * @param target Nuevo umbral de interacciones
     */
    fun onOptionSelected(target: Int) {
        updateFeedbackUseCase(target) //Esto resetea el contador a 0 y cambia el target
        isSheetVisible = false
    }
}