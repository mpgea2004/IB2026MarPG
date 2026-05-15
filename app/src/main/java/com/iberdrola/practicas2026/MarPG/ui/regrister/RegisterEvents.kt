package com.iberdrola.practicas2026.MarPG.ui.regrister

data class RegisterEvents(
    val onNameChange: (String) -> Unit,
    val onEmailChange: (String) -> Unit,
    val onPasswordChange: (String) -> Unit,
    val onRegisterClick: (() -> Unit) -> Unit,
    val onNavigateToLogin: () -> Unit,
    val onConfirmPasswordChange: (String) -> Unit,
    val onTogglePasswordVisibility: () -> Unit,
    val onToggleConfirmPasswordVisibility: () -> Unit,
)