package com.iberdrola.practicas2026.MarPG.data.network

import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Interfaz de Retrofit para el acceso a servicios externos (Mockoon)
 */
interface InvoiceApiServer {
    /**
     * Obtiene la lista de facturas desde el servidor
     * Endpoint configurado: http://10.0.2.2:3000/invoices (IP local del emulador)
     */
    @GET("invoices")
    suspend fun getInvoices(): InvoiceResponse

    @POST("invoices/{id}/pay")
    suspend fun payInvoice(@Path("id") id: String)
}