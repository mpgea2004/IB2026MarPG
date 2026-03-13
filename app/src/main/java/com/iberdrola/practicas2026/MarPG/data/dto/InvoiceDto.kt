package com.iberdrola.practicas2026.MarPG.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la deserialización de facturas desde la API o archivo local
 * Representa la estructura de datos cruda recibida de la fuente externa
 */
data class InvoiceDto(
    @SerializedName("id") val id: String,
    @SerializedName("contractType") val contractType: String, // Tipo de contrato (Luz/Gas)
    @SerializedName("amount") val amount: Double, // Importe total
    @SerializedName("startDate") val startDate: String, // Inicio periodo facturación
    @SerializedName("endDate") val endDate: String, // Fin periodo facturación
    @SerializedName("issueDate") val issueDate: String, // Fecha de emisión
    @SerializedName("status") val status: String // Estado (Pagada/Pendiente)
)