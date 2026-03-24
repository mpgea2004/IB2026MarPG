package com.iberdrola.practicas2026.MarPG.data.model

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto

/**
 * Clase de envoltorio (wrapper) para la respuesta de la API o el JSON local
 * Facilita la recepción de la lista de facturas mediante Retrofit o Gson
 */
data class InvoiceResponse(
    val invoices: List<InvoiceDto>
)
