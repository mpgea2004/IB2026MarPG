package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.data.repository.FeedbackRepository
import javax.inject.Inject

class UpdateFeedbackConfigUseCase @Inject constructor(
    private val repository: FeedbackRepository
) {
    operator fun invoke(newTarget: Int) {
        repository.feedbackTarget = newTarget
        repository.feedbackCount = 0 //Al actualizar la tregua, reseteo el conteo
    }
}