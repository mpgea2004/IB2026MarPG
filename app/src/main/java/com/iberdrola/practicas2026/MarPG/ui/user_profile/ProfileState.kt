package com.iberdrola.practicas2026.MarPG.ui.user_profile

import androidx.annotation.StringRes

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val password: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val phoneError: Int? = null,
    @StringRes val passwordError: Int? = null,
    val isSaved: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveJustFinished: Boolean = false
)
