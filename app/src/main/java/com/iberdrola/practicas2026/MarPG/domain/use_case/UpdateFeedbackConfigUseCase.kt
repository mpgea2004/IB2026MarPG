package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import javax.inject.Inject

/**
 * Caso de uso para actualizar la configuración de la tregua de feedback
 * Ajusta el umbral de interacciones y reinicia el progreso actual
 */
class UpdateFeedbackConfigUseCase @Inject constructor(
    private val repository: FeedbackRepository
) {
    /**
     * Actualiza el objetivo de feedback y resetea el contador a cero
     * @param newTarget El nuevo número de interacciones requeridas (ej: 1, 3 o 10)
     */
    operator fun invoke(newTarget: Int) {
        repository.feedbackTarget = newTarget
        repository.feedbackCount = 0 //Al actualizar la tregua, reseteo el conteo
    }
}