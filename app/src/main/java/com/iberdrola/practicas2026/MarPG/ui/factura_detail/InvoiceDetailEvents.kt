package com.iberdrola.practicas2026.MarPG.ui.factura_detail

data class InvoiceDetailEvents(
    val onBack: () -> Unit = {},
    val onDownloadPdf: () -> Unit = {},
    val onPay: () -> Unit = {},
    val onDismissPdf: () -> Unit = {}
)