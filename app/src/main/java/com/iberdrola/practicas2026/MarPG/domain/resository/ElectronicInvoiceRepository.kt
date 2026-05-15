package com.iberdrola.practicas2026.MarPG.domain.repository

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import kotlinx.coroutines.flow.Flow

interface ElectronicInvoiceRepository {
    fun getAllElectronicInvoice(isCloud: Boolean): Flow<List<ElectronicInvoice>>

    suspend fun updateElectronicInvoice(electronicInvoice: ElectronicInvoice)
}