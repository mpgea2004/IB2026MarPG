package com.iberdrola.practicas2026.MarPG.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Entidad que representa una factura en la base de datos local (Room)
 * Define la estructura de la tabla "invoices"
 */
@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String, // Identificador único de la factura
    val contractType: String, // Tipo de contrato: "LUZ" o "GAS"
    val amount: Double, // Importe total de la factura
    val startDate: String, // Fecha de inicio del periodo
    val endDate: String, // Fecha de fin del periodo
    val issueDate: String, // Fecha de emisión de factura
    val status: String // Estado: "Pagadas", "Pendientes de Pago", "En trámite de cobro", "Anuladas", "Cuota Fija"
)