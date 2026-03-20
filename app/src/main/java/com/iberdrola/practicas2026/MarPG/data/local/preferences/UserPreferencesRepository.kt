package com.iberdrola.practicas2026.MarPG.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val PHONE = stringPreferencesKey("phone")
        val ADDRESS = stringPreferencesKey("address")
        val PASSWORD = stringPreferencesKey("password")
    }

    val userProfileFlow: Flow<ProfileState> = context.dataStore.data.map { prefs ->
        ProfileState(
            name = prefs[PreferencesKeys.NAME] ?: "",
            email = prefs[PreferencesKeys.EMAIL] ?: "",
            phone = prefs[PreferencesKeys.PHONE] ?: "",
            address = prefs[PreferencesKeys.ADDRESS] ?: "",
            password = prefs[PreferencesKeys.PASSWORD] ?: ""
        )
    }

    suspend fun updateProfile(state: ProfileState) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NAME] = state.name
            prefs[PreferencesKeys.EMAIL] = state.email
            prefs[PreferencesKeys.PHONE] = state.phone
            prefs[PreferencesKeys.ADDRESS] = state.address
            prefs[PreferencesKeys.PASSWORD] = state.password
        }
    }

    suspend fun updatePhone(newPhone: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PHONE] = newPhone
        }
    }

    suspend fun updatePassword(newPass: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PASSWORD] = newPass
        }
    }
}