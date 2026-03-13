package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import javax.inject.Inject

class CheckFeedbackUseCase @Inject constructor(
    private val repository: FeedbackRepository
) {
    /**
     * Incrementa el contador y devuelve true si debe mostrarse el BottomSheet
     */
    operator fun invoke(): Boolean {
        repository.feedbackCount++//sumo

        //Si el contador llega al objetivo, aviso de que toca mostrarlo
        return if (repository.feedbackCount >= repository.feedbackTarget) {
            true
        } else {
            false
        }
    }
}