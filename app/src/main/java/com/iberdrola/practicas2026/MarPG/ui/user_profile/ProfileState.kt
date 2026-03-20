package com.iberdrola.practicas2026.MarPG.ui.user_profile

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val password: String = "",
    val emailError: String? = null,
    val phoneError: String? = null
)