package com.iberdrola.practicas2026.MarPG.domain.use_case.users

import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidatePhoneUseCase
import javax.inject.Inject

class ValidateProfileUseCase @Inject constructor(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePhone: ValidatePhoneUseCase
) {
    operator fun invoke(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): ProfileValidationResult {
        val isNameEmpty = name.trim().isEmpty()
        val isEmailEmpty = email.trim().isEmpty()
        val isEmailInvalid = !validateEmail(email)
        val isPasswordEmpty = password.trim().isEmpty()
        val isPasswordTooShort = password.trim().isNotEmpty() && password.trim().length < 6
        val passwordsMatch = password == confirmPassword
        val isPhoneInvalid = phone.isNotEmpty() && !validatePhone(phone)

        if (!isNameEmpty && !isEmailEmpty && !isEmailInvalid && !isPasswordEmpty && !isPasswordTooShort && passwordsMatch && !isPhoneInvalid) {
            return ProfileValidationResult.Success
        }

        return ProfileValidationResult.Error(
            nameError = if (isNameEmpty) R.string.error_field_required else null,
            emailError = when {
                isEmailEmpty -> R.string.error_field_required
                isEmailInvalid -> R.string.error_invalid_email_format
                else -> null
            },
            passwordError = when {
                isPasswordEmpty -> R.string.error_field_required
                isPasswordTooShort -> R.string.error_password_too_short
                else -> null
            },
            confirmPasswordError = if (!passwordsMatch) R.string.error_passwords_do_not_match else null,
            phoneError = if (isPhoneInvalid) R.string.error_invalid_phone_format else null
        )
    }
}

sealed class ProfileValidationResult {
    data object Success : ProfileValidationResult()
    data class Error(
        val nameError: Int? = null,
        val emailError: Int? = null,
        val phoneError: Int? = null,
        val passwordError: Int? = null,
        val confirmPasswordError: Int? = null
    ) : ProfileValidationResult()
}
