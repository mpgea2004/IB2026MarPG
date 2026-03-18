package com.iberdrola.practicas2026.MarPG.domain.use_case.feedback

import com.iberdrola.practicas2026.MarPG.data.local.preferences.FeedbackDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckFeedbackUseCase @Inject constructor(
    private val dataStore: FeedbackDataStore
) {
    // Para que el ViewModel observe si ha llegado a 0
    fun shouldShowFeedback(): Flow<Boolean> = dataStore.remainingAttempts.map { it <= 0 }

    // El "atrás" de la pantalla de facturas
    suspend fun notifyBackPress() = dataStore.decrementAttempts()

    // Configurar la siguiente tregua
    suspend fun setNextTregua(value: Int) = dataStore.resetTo(value)
}