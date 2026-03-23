package com.iberdrola.practicas2026.MarPG.ui.factura_home

data class HomeEvents(
    val onProfileClick: () -> Unit = {},
    val onNavigateToInvoices: () -> Unit = {},
    val onNavigateToElectronicInvoice: () -> Unit = {},
    val onToggleCloud: (Boolean) -> Unit = {},
    val onFeedbackOption: (Int) -> Unit = {},
    val onDismissFeedback: () -> Unit = {}
)