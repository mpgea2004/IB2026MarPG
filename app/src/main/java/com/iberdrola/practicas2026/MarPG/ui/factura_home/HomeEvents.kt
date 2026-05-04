package com.iberdrola.practicas2026.MarPG.ui.factura_home

data class HomeEvents(
    val onNavigateToInvoices: () -> Unit = {},
    val onNavigateToElectronicInvoice: () -> Unit = {},
    val onNavigateToProfile: () -> Unit = {},
    val onNavigateToFaq: () -> Unit = {},
    val onToggleCloud: (Boolean) -> Unit = {},
    val onSheetDismiss: () -> Unit = {},
    val onSheetOptionSelected: (Int) -> Unit = {},
    val onSheetDontAskAgain: () -> Unit = {}
)
