package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus

/**
 * Mappers para la conversión entre capas de datos y dominio
 * Facilita la transformación de [InvoiceEntity] e [InvoiceDto] al modelo [Invoice]
 */

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
        //Convierto a la enum de estados
        status = this.status.toInvoiceStatus()
    )
}
/**
 * Convierte una lista de DTOs a una lista de modelos de dominio
 */
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
        status = this.status.toInvoiceStatus()
    )
}

/**
 * MAPPER 3: De DTO (API) a Entidad de Base de Datos (Room)
 * Este es fundamental para guardar los datos en la caché.
 */
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
/**
 * Convierte una lista de DTOs a una lista de entidades para Room
 */
fun List<InvoiceDto>.toEntityList() = this.map { it.toEntity() }

/**
 * Convierte los strings del JSON/DB al Enum de Dominio
 */
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