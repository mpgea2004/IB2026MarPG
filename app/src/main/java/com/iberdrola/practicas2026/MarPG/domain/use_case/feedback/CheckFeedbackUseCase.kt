package com.iberdrola.practicas2026.MarPG.domain.use_case.feedback

import com.iberdrola.practicas2026.MarPG.data.local.preferences.FeedbackDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class CheckFeedbackUseCase @Inject constructor(
    private val dataStore: FeedbackDataStore
) {
    fun shouldShowFeedback(): Flow<Boolean> = combine(
        dataStore.remainingAttempts,
        dataStore.shouldAskFeedback
    ) { attempts, shouldAsk ->
        shouldAsk && attempts <= 0
    }

    suspend fun notifyBackPress() = dataStore.decrementAttempts()

    suspend fun setNextTregua(value: Int) = dataStore.resetTo(value)

    suspend fun dontAskAgain() = dataStore.disableFeedback()
}