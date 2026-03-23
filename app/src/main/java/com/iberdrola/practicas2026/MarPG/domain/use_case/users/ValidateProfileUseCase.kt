package com.iberdrola.practicas2026.MarPG.domain.use_case.users

import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidatePhoneUseCase
import javax.inject.Inject

class ValidateProfileUseCase @Inject constructor(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePhone: ValidatePhoneUseCase
) {
    operator fun invoke(email: String, phone: String): ProfileValidationResult {
        val isEmailValid = email.isEmpty() || validateEmail(email)
        val isPhoneValid = phone.isEmpty() || validatePhone(phone)

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