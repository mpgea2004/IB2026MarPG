package com.iberdrola.practicas2026.MarPG.domain.resository

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun getAllInvoices(isCloud: Boolean): Flow<List<Invoice>>

    fun getInvoiceById(id: String): Flow<Invoice?>

    suspend fun payInvoice(id: String, isCloud: Boolean)
}