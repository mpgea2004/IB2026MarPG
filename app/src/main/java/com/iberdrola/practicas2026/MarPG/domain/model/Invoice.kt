package com.iberdrola.practicas2026.MarPG.domain.model

enum class ContractType {
    LUZ,
    GAS
}
enum class InvoiceStatus(val description: String) {
    PAGADAS("Pagadas"),
    PENDIENTES_PAGO("Pendientes de Pago"),
    EN_TRAMITE("En trámite de cobro"),
    ANULADAS("Anuladas"),
    CUOTA_FIJA("Cuota Fija");

    companion object {
        fun getAllDescriptions(): List<String> {
            return entries.map { it.description }
        }
    }
}

data class Invoice(
    val id: String,
    val contractType: ContractType,
    val amount: Double,
    val startDate: String,
    val endDate: String,
    val issueDate: String,
    val status: InvoiceStatus
)