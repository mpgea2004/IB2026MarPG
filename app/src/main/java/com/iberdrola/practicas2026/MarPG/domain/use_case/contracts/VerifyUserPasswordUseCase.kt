package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VerifyUserPasswordUseCase @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) {
    suspend operator fun invoke(passwordInput: String): Boolean {
        val currentProfile = userPrefs.userProfileFlow.first()
        val savedPassword = currentProfile.password

        val validPassword = savedPassword.ifEmpty { "1234" }

        return passwordInput == validPassword
    }
}