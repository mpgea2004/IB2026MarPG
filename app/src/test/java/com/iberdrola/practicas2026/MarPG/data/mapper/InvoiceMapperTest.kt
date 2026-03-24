package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class InvoiceMapperTest {

    @Test
    fun `InvoiceEntity toDomain mapea correctamente tipos LUZ y GAS`() {
        val entityLuz = createInvoiceEntity(id = "1", type = "LUZ")
        val entityGas = createInvoiceEntity(id = "2", type = "GAS")

        val domainLuz = entityLuz.toDomain()
        val domainGas = entityGas.toDomain()

        assertEquals(ContractType.LUZ, domainLuz.contractType)
        assertEquals(ContractType.GAS, domainGas.contractType)
    }

    @Test
    fun `InvoiceDto toDomain maneja minúsculas en el tipo de contrato`() {
        val dto = createInvoiceDto(type = "luz")

        val domain = dto.toDomain()

        assertEquals(ContractType.LUZ, domain.contractType)
    }

    @Test
    fun `toInvoiceStatus mapea todos los strings conocidos correctamente`() {
        val estados = listOf("Pagadas", "Pendientes de Pago", "En trámite de cobro", "Anuladas", "Cuota Fija")

        assertEquals(InvoiceStatus.PAGADAS, createInvoiceDto(status = "Pagadas").toDomain().status)
        assertEquals(InvoiceStatus.PENDIENTES_PAGO, createInvoiceDto(status = "Pendientes de Pago").toDomain().status)
        assertEquals(InvoiceStatus.EN_TRAMITE, createInvoiceDto(status = "En trámite de cobro").toDomain().status)
        assertEquals(InvoiceStatus.ANULADAS, createInvoiceDto(status = "Anuladas").toDomain().status)
        assertEquals(InvoiceStatus.CUOTA_FIJA, createInvoiceDto(status = "Cuota Fija").toDomain().status)
    }

    @Test
    fun `toInvoiceStatus devuelve ANULADAS por defecto ante un string desconocido`() {
        val dto = createInvoiceDto(status = "Estado Inventado")

        val domain = dto.toDomain()

        assertEquals(InvoiceStatus.ANULADAS, domain.status)
    }

    @Test
    fun `toEntityList convierte correctamente una lista de DTOs para Room`() {
        val dtoList = listOf(
            createInvoiceDto(id = "1"),
            createInvoiceDto(id = "2")
        )

        val entityList = dtoList.toEntityList()

        assertEquals(2, entityList.size)
        assertEquals("1", entityList[0].id)
        assertEquals("2", entityList[1].id)
    }

    private fun createInvoiceEntity(id: String = "1", type: String = "LUZ") = InvoiceEntity(
        id = id,
        contractType = type,
        amount = 50.0,
        startDate = "01/01/2024",
        endDate = "31/01/2024",
        issueDate = "05/02/2024",
        status = "Pagadas"
    )

    private fun createInvoiceDto(id: String = "1", type: String = "LUZ", status: String = "Pagadas") = InvoiceDto(
        id = id,
        contractType = type,
        amount = 50.0,
        startDate = "01/01/2024",
        endDate = "31/01/2024",
        issueDate = "05/02/2024",
        status = status
    )
}