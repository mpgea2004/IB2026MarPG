package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.annotation.StringRes

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val password: String = "",
    @StringRes val emailError: Int? = null,
    @StringRes val phoneError: Int? = null
)
