package com.iberdrola.practicas2026.MarPG.ui.regrister

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val nameError: String? = null,
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
)