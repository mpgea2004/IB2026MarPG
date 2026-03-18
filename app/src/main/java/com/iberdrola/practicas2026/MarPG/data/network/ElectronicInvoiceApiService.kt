package com.iberdrola.practicas2026.MarPG.data.network

import com.iberdrola.practicas2026.MarPG.data.dto.ElectronicInvoiceDto
import retrofit2.http.GET

interface ElectronicInvoiceApiService {
    @GET("electronicInvoices")
    suspend fun getElectronicInvoice(): List<ElectronicInvoiceDto>
}