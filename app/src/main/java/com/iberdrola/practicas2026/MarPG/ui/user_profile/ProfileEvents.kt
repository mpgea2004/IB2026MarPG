package com.iberdrola.practicas2026.MarPG.ui.user_profile


data class ProfileEvents(
    val onNameChanged: (String) -> Unit = {},
    val onEmailChanged: (String) -> Unit = {},
    val onPhoneChanged: (String) -> Unit = {},
    val onSaveClick: (onSuccess: () -> Unit) -> Unit,
    val onBackClick: () -> Unit = {},
    val onAddressChanged: (String) -> Unit = {},
    val onPasswordChanged: (String) -> Unit = {},
    val onLogout: () -> Unit = {},
    val onEditClick: () -> Unit = {},
    val onDiscardClick: () -> Unit = {},
    val onSecurityPasswordChanged: (String) -> Unit = {},
    val onSecurityConfirmClick: () -> Unit = {},
    val onSecurityDismiss: () -> Unit = {},
    val onToggleSecurityPasswordVisibility: () -> Unit = {}
)