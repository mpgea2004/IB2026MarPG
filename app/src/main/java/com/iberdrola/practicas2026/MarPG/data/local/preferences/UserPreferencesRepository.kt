package com.iberdrola.practicas2026.MarPG.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
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
        val AMOUNT_VISIBLE = booleanPreferencesKey("amount_visible")
        
        val OTP_RESEND_ATTEMPTS_LUZ = intPreferencesKey("otp_resend_attempts_luz")
        val LAST_OTP_RESEND_TIMESTAMP_LUZ = longPreferencesKey("last_otp_resend_timestamp_luz")
        
        val OTP_RESEND_ATTEMPTS_GAS = intPreferencesKey("otp_resend_attempts_gas")
        val LAST_OTP_RESEND_TIMESTAMP_GAS = longPreferencesKey("last_otp_resend_timestamp_gas")
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

    val amountVisibleFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferencesKeys.AMOUNT_VISIBLE] ?: true
    }

    fun getOtpResendDataFlow(type: ContractType): Flow<Pair<Int, Long>> = context.dataStore.data.map { prefs ->
        when (type) {
            ContractType.LUZ -> Pair(
                prefs[PreferencesKeys.OTP_RESEND_ATTEMPTS_LUZ] ?: 3,
                prefs[PreferencesKeys.LAST_OTP_RESEND_TIMESTAMP_LUZ] ?: 0L
            )
            ContractType.GAS -> Pair(
                prefs[PreferencesKeys.OTP_RESEND_ATTEMPTS_GAS] ?: 3,
                prefs[PreferencesKeys.LAST_OTP_RESEND_TIMESTAMP_GAS] ?: 0L
            )
        }
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

    suspend fun clearProfile() {
        context.dataStore.edit { prefs ->
            prefs.clear()
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

    suspend fun updateAddress(newAddress: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ADDRESS] = newAddress
        }
    }

    suspend fun updateAmountVisibility(visible: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AMOUNT_VISIBLE] = visible
        }
    }

    suspend fun updateOtpResendData(type: ContractType, attempts: Int, timestamp: Long) {
        context.dataStore.edit { prefs ->
            when (type) {
                ContractType.LUZ -> {
                    prefs[PreferencesKeys.OTP_RESEND_ATTEMPTS_LUZ] = attempts
                    prefs[PreferencesKeys.LAST_OTP_RESEND_TIMESTAMP_LUZ] = timestamp
                }
                ContractType.GAS -> {
                    prefs[PreferencesKeys.OTP_RESEND_ATTEMPTS_GAS] = attempts
                    prefs[PreferencesKeys.LAST_OTP_RESEND_TIMESTAMP_GAS] = timestamp
                }
            }
        }
    }
}