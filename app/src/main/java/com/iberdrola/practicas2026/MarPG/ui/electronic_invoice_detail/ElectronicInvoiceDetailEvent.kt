package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice

data class ElectronicInvoiceEvents(
    val onSelectContract: (ElectronicInvoice) -> Unit = {},
    val onEmailChange: (String) -> Unit = {},
    val onOtpChange: (String) -> Unit = {},
    val onLegalCheckChange: (Boolean) -> Unit = {},
    val onConfirmUpdate: () -> Unit = {},
    val onConfirmDeactivate: () -> Unit = {},
    val onResendOtp: () -> Unit = {},
    val onExitFlow: () -> Unit = {},
    val onBack: () -> Unit = {},
    val onNext: () -> Unit = {},
    val onCloseBanner: () -> Unit = {},
    val onShowLegal: (String, String) -> Unit = {_,_ -> },
    val onDismissLegal: () -> Unit = {}
)