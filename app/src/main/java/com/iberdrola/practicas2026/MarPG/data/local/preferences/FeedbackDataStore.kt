package com.iberdrola.practicas2026.MarPG.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Defino la extensión aquí. El nombre del archivo físico será "feedback_prefs.preferences_pb"
private val Context.dataStore by preferencesDataStore(name = "feedback_prefs")

@Singleton
class FeedbackDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val REMAINING_ATTEMPTS = intPreferencesKey("remaining_attempts")    }

    // Leo cuántos intentos faltan (por defecto 1 la primera vez)
    val remainingAttempts: Flow<Int> = context.dataStore.data
        .map { it[REMAINING_ATTEMPTS] ?: 1 }

    // Restamos 1 al contador
    suspend fun decrementAttempts() {
        context.dataStore.edit { prefs ->
            val current = prefs[REMAINING_ATTEMPTS] ?: 1
            if (current > 0) prefs[REMAINING_ATTEMPTS] = current - 1
        }
    }

    // Guardamos el nuevo valor (1, 3 o 10)
    suspend fun resetTo(value: Int) {
        context.dataStore.edit { it[REMAINING_ATTEMPTS] = value }
    }
}