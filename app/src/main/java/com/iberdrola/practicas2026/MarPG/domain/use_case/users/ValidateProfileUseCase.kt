package com.iberdrola.practicas2026.MarPG.domain.use_case.users

import javax.inject.Inject

class ValidateProfileUseCase @Inject constructor() {
    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$")

    operator fun invoke(email: String, phone: String): ProfileValidationResult {
        val isEmailValid = emailPattern.matches(email)
        val isPhoneValid = phone.length == 9 && phone.all { it.isDigit() }

        return if (isEmailValid && isPhoneValid) {
            ProfileValidationResult.Success
        } else {
            ProfileValidationResult.Error(
                emailError = if (!isEmailValid) "El formato del correo no es válido" else null,
                phoneError = if (!isPhoneValid) "El teléfono debe tener 9 dígitos" else null
            )
        }
    }
}

sealed class ProfileValidationResult {
    object Success : ProfileValidationResult()
    data class Error(val emailError: String?, val phoneError: String?) : ProfileValidationResult()
}