package com.iberdrola.practicas2026.MarPG.ui.factura_detail

data class InvoiceDetailEvents(
    val onBack: () -> Unit = {},
    val onDownloadPdf: () -> Unit = {},
    val onPayClick: () -> Unit = {},
    val onPayConfirm: () -> Unit = {},
    val onPasswordChange: (String) -> Unit = {},
    val onDismissPasswordDialog: () -> Unit = {},
    val onDismissOverdueDialog: () -> Unit = {},
    val onDismissPdf: () -> Unit = {},
    val onToggleAmountVisibility: () -> Unit = {}
)