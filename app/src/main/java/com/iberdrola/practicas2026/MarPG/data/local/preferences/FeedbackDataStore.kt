package com.iberdrola.practicas2026.MarPG.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "feedback_prefs")

@Singleton
class FeedbackDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val REMAINING_ATTEMPTS = intPreferencesKey("remaining_attempts")
        private val SHOULD_ASK_FEEDBACK = booleanPreferencesKey("should_ask_feedback")
    }

    val remainingAttempts: Flow<Int> = context.dataStore.data
        .map { it[REMAINING_ATTEMPTS] ?: 1 }

    val shouldAskFeedback: Flow<Boolean> = context.dataStore.data
        .map { it[SHOULD_ASK_FEEDBACK] ?: true }

    suspend fun decrementAttempts() {
        context.dataStore.edit { prefs ->
            val current = prefs[REMAINING_ATTEMPTS] ?: 1
            if (current > 0) prefs[REMAINING_ATTEMPTS] = current - 1
        }
    }

    suspend fun resetTo(value: Int) {
        context.dataStore.edit { it[REMAINING_ATTEMPTS] = value }
    }

    suspend fun disableFeedback() {
        context.dataStore.edit { it[SHOULD_ASK_FEEDBACK] = false }
    }
}