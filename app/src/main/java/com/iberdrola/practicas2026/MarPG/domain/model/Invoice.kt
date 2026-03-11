package com.iberdrola.practicas2026.MarPG.domain.model

enum class ContractType {
    LUZ,
    GAS
}

enum class InvoiceStatus(val description: String) {
    PAID("Pagada"),
    PENDING("Pendiente de pago")
}

data class Invoice(
    val id: String,
    val contractType: ContractType,
    val amount: Double,
    val startDate: String,  // Ejemplo: "01/01/2026"
    val endDate: String,     // Ejemplo: "31/01/2026"
    val issueDate: String, // Ejemplo: "05/02/2026"
    val status: InvoiceStatus
)