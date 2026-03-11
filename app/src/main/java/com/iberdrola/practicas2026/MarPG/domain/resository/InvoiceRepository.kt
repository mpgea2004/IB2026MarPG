package com.iberdrola.practicas2026.MarPG.domain.resository

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    /**
     * Me devuelve todas las facturas en un flujo de datos
     */
    fun getAllInvoices(): Flow<List<Invoice>>
}