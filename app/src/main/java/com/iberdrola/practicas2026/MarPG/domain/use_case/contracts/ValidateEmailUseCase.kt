package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    operator fun invoke(email: String): Boolean {
        return email.isNotBlank() && emailPattern.matches(email.trim())
    }
}