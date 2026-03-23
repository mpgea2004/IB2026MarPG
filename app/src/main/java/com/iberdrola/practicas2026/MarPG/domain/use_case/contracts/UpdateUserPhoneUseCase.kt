package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import javax.inject.Inject

class UpdateUserPhoneUseCase @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val validatePhone: ValidatePhoneUseCase
) {
    suspend operator fun invoke(newPhone: String): Boolean {
        return if (validatePhone(newPhone)) {
            userPrefs.updatePhone(newPhone)
            true
        } else {
            false
        }
    }
}