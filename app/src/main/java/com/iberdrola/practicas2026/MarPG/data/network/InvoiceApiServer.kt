package com.iberdrola.practicas2026.MarPG.data.network

import com.iberdrola.practicas2026.MarPG.data.model.InvoiceResponse
import retrofit2.http.GET

/**
 * Interfaz de Retrofit para el acceso a servicios externos (Mockoon)
 * Gestiona las peticiones de red relacionadas con las facturas
 */
interface InvoiceApiServer {
    /**
     * Obtiene la lista de facturas desde el servidor
     * Endpoint configurado: http://10.0.2.2:3000/invoices (IP local del emulador)
     */
    @GET("invoices")
    suspend fun getInvoices(): InvoiceResponse
}