package com.iberdrola.practicas2026.MarPG.data.network

import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import retrofit2.http.GET

interface InvoiceApiServer {
    // Esto buscará en http://10.0.2.2:3000/invoices (la IP del emulador)
    @GET("invoices")
    suspend fun getInvoices(): InvoiceResponse
}