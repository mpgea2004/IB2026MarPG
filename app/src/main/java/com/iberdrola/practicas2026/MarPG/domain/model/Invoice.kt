package com.iberdrola.practicas2026.MarPG.domain.model

/**
 * Representa los tipos de suministros ofrecidos por Iberdrola
 */
enum class ContractType {
    LUZ,
    GAS
}

/**
 * Estados posibles de una factura con su descripción exacta según requerimientos.
 * La descripción se usa para mostrar en la UI y para mapear desde el JSON.
 */
enum class InvoiceStatus(val description: String) {
    PAGADAS("Pagadas"),
    PENDIENTES_PAGO("Pendientes de Pago"),
    EN_TRAMITE("En trámite de cobro"),
    ANULADAS("Anuladas"),
    CUOTA_FIJA("Cuota Fija"),
    UNKNOWN("Desconocido");

    //Método para obtener todas las descripciones de golpe para los filtros
    companion object {
        fun getAllDescriptions(): List<String> {
            return entries.map { it.description }
        }
    }
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