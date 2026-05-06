package com.iberdrola.practicas2026.MarPG.ui.factura_home

data class HomeState(
    val userName: String = "",
    val isProfileComplete: Boolean = false,
    val isFullProfileComplete: Boolean = false,
    val isSheetVisible: Boolean = false,
    val isCloudEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isGasEnabled: Boolean? = null
)
