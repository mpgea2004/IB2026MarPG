package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice

data class InvoiceDetailState(
    val invoice: Invoice? = null,
    val isLoading: Boolean = false,
    val isDownloadingPdf: Boolean = false,
    val pdfDownloaded: Boolean = false,
    val paymentSuccess: Boolean = false,
    val paymentError: Boolean = false
)