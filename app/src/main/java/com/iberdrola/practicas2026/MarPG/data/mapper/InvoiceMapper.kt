package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus

/**
 * MAPPER 1: Para la Base de Datos(Room)
 */
fun InvoiceEntity.toDomain(): Invoice {
    return Invoice(
        id = this.id,
        //Convierto a la enum, luz o gas
        contractType = if(this.contractType == "LUZ") ContractType.LUZ else ContractType.GAS,
        amount = this.amount,
        startDate = this.startDate,
        endDate = this.endDate,
        issueDate = this.issueDate,
        //Convierto a la enum, paid o pending
        status = if (this.status.uppercase() == "PAID") InvoiceStatus.PAID else InvoiceStatus.PENDING
    )
}

fun List<InvoiceDto>.toDomainList() = this.map { it.toDomain() }
/**
 * MAPPER 2: Para el JSON(assets/Api)
 */
fun InvoiceDto.toDomain(): Invoice{
    return Invoice(
        id = this.id,
        contractType = if(this.contractType.uppercase() == "LUZ") ContractType.LUZ else ContractType.GAS,
        amount = this.amount,
        startDate = this.startDate,
        endDate = this.endDate,
        issueDate = this.issueDate,
        status = if (this.status.uppercase() == "PAID") InvoiceStatus.PAID else InvoiceStatus.PENDING
    )
}