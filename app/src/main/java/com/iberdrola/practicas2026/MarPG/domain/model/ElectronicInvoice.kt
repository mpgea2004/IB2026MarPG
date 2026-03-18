package com.iberdrola.practicas2026.MarPG.domain.model

data class ElectronicInvoice(
    val id: String,
    val type: ContractType,
    val isEnabled: Boolean,
    val email: String?
)