package com.iberdrola.practicas2026.MarPG.ui.login

data class LoginEvents(
    val onEmailChanged: (String) -> Unit = {},
    val onPasswordChanged: (String) -> Unit = {},
    val onLoginClick: (onSuccess: () -> Unit) -> Unit = {},
    val onRegisterClick: () -> Unit = {},
    val onTogglePasswordVisibility: () -> Unit = {},
)
