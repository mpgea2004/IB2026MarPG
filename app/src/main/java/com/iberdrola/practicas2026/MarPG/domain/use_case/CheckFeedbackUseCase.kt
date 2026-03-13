package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import javax.inject.Inject

/**
 * Caso de uso para verificar si se debe mostrar el diálogo de feedback
 * Encapsula la lógica de incremento y validación del contador
 */
class CheckFeedbackUseCase @Inject constructor(
    private val repository: FeedbackRepository
) {
    /**
     * Incrementa el contador y evalúa si se ha alcanzado el objetivo de visitas
     * @return True si el contador iguala o supera la tregua fijada
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