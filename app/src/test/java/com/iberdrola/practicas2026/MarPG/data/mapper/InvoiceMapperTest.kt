package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.InvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class InvoiceMapperTest {

    @Test
    fun `InvoiceEntity toDomain mapea bien tipos LUZ y GAS`() {
        val entityLuz = createInvoiceEntity(id = "1", type = "LUZ")
        val entityGas = createInvoiceEntity(id = "2", type = "GAS")

        val domainLuz = entityLuz.toDomain()
        val domainGas = entityGas.toDomain()

        assertEquals(ContractType.LUZ, domainLuz.contractType)
        assertEquals(ContractType.GAS, domainGas.contractType)
    }

    @Test
    fun `InvoiceDto toDomain maneja case insensitive en el tipo de contrato`() {
        val dto = createInvoiceDto(type = "luz")
        val domain = dto.toDomain()
        assertEquals(ContractType.LUZ, domain.contractType)

        val dto2 = createInvoiceDto(type = "GAS")
        val domain2 = dto2.toDomain()
        assertEquals(ContractType.GAS, domain2.contractType)
    }

    @Test
    fun `toInvoiceStatus mapea todos los strings conocidos correctamente`() {
        assertEquals(InvoiceStatus.PAGADAS, createInvoiceDto(status = "Pagadas").toDomain().status)
        assertEquals(InvoiceStatus.PENDIENTES_PAGO, createInvoiceDto(status = "Pendientes de Pago").toDomain().status)
        assertEquals(InvoiceStatus.EN_TRAMITE, createInvoiceDto(status = "En trámite de cobro").toDomain().status)
        assertEquals(InvoiceStatus.ANULADAS, createInvoiceDto(status = "Anuladas").toDomain().status)
        assertEquals(InvoiceStatus.CUOTA_FIJA, createInvoiceDto(status = "Cuota Fija").toDomain().status)
    }

    @Test
    fun `toInvoiceStatus devuelve ANULADAS por defecto ante un string desconocido`() {
        val dto = createInvoiceDto(status = "Estado Desconocido")
        val domain = dto.toDomain()
        assertEquals(InvoiceStatus.ANULADAS, domain.status)
    }

    @Test
    fun `toEntityList convierte correctamente una lista de dtos para Room manteniendo mayusculas`() {
        val dtoList = listOf(
            createInvoiceDto(id = "1", type = "luz"),
            createInvoiceDto(id = "2", type = "gas")
        )

        val entityList = dtoList.toEntityList()

        assertEquals(2, entityList.size)
        assertEquals("LUZ", entityList[0].contractType)
        assertEquals("GAS", entityList[1].contractType)
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
