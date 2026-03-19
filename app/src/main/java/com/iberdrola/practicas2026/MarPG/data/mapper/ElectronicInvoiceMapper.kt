package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.ElectronicInvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType

fun ElectronicInvoiceEntity.toDomain() = ElectronicInvoice(
    id = id,
    type = try {
        ContractType.valueOf(type.uppercase())
    } catch (e: Exception) {
        ContractType.LUZ
    },
    isEnabled = isEnabled,
    email = email
)

fun ElectronicInvoiceDto.toEntity(): ElectronicInvoiceEntity {
    return ElectronicInvoiceEntity(
        id = this.id,
        type = this.type,
        isEnabled = this.isEnabled,
        email = this.email
    )
}

fun ElectronicInvoice.toEntity(): ElectronicInvoiceEntity {
    return ElectronicInvoiceEntity(
        id = this.id,
        type = this.type.name,
        isEnabled = this.isEnabled,
        email = this.email
    )
}