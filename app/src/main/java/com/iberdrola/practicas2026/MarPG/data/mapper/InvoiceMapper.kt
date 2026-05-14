package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = this.id,
        contractType = if(this.contractType == "LUZ") ContractType.LUZ else ContractType.GAS,
        amount = this.amount,
        startDate = this.startDate,
        endDate = this.endDate,
        issueDate = this.issueDate,
        status = this.status.toInvoiceStatus()
    )
}

fun List<InvoiceDto>.toDomainList() = this.map { it.toDomain() }

fun InvoiceDto.toDomain(): Invoice{
    return Invoice(
        id = this.id,
        contractType = if(this.contractType.uppercase() == "LUZ") ContractType.LUZ else ContractType.GAS,
        amount = this.amount,
        startDate = this.startDate,
        endDate = this.endDate,
        issueDate = this.issueDate,
        status = this.status.toInvoiceStatus()
    )
}

fun InvoiceDto.toEntity(): InvoiceEntity {
    return InvoiceEntity(
        id = this.id,
        contractType = this.contractType.uppercase(),
        amount = this.amount,
        startDate = this.startDate,
        endDate = this.endDate,
        issueDate = this.issueDate,
        status = this.status
    )
}

fun List<InvoiceDto>.toEntityList() = this.map { it.toEntity() }

private fun String.toInvoiceStatus(): InvoiceStatus {
    return when (this) {
        "Pagadas" -> InvoiceStatus.PAGADAS
        "Pendientes de Pago" -> InvoiceStatus.PENDIENTES_PAGO
        "En trámite de cobro" -> InvoiceStatus.EN_TRAMITE
        "Anuladas" -> InvoiceStatus.ANULADAS
        "Cuota Fija" -> InvoiceStatus.CUOTA_FIJA
        else -> InvoiceStatus.ANULADAS
    }
}