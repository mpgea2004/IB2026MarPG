package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import android.net.Uri
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice

data class InvoiceDetailState(
    val invoice: Invoice? = null,
    val isLoading: Boolean = false,
    val isDownloadingPdf: Boolean = false,
    val pdfDownloaded: Boolean = false,
    val pdfError: Boolean = false,
    val loadError: Boolean = false,
    val paymentSuccess: Boolean = false,
    val paymentError: Boolean = false,
    val pdfUri: Uri? = null,
    val showPdfViewer: Boolean = false,
    val showPayPasswordDialog: Boolean = false,
    val showOverdueDialog: Boolean = false,
    val payPasswordInput: String = "",
    val payPasswordError: Boolean = false,
    val isOverdue: Boolean = false,
    val isAmountVisible: Boolean = true
)