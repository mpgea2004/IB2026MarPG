package com.iberdrola.practicas2026.MarPG.data.model

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto

data class InvoiceResponse(
    val invoices: List<InvoiceDto>
)
