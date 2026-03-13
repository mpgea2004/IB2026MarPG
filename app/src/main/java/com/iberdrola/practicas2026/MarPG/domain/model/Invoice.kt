package com.iberdrola.practicas2026.MarPG.domain.model

/**
 * Representa los tipos de suministros ofrecidos por Iberdrola
 */
enum class ContractType {
    LUZ,
    GAS
}

/**
 * Estados posibles de una factura con su descripción
 */
enum class InvoiceStatus(val description: String) {
    PAID("Pagada"),
    PENDING("Pendiente de pago")
}

/**
 * Modelo de dominio de una Factura
 * Representa la entidad de negocio pura, independiente de APIs o Bases de Datos
 */
data class Invoice(
    val id: String, // Identificador único
    val contractType: ContractType, // LUZ o GAS
    val amount: Double, // Importe total
    val startDate: String,  // Ejemplo: "01/01/2026"
    val endDate: String,     // Ejemplo: "31/01/2026"
    val issueDate: String, // Ejemplo: "05/02/2026"
    val status: InvoiceStatus // PAID o PENDING
)