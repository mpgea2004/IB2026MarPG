package com.iberdrola.practicas2026.MarPG.data.mapper

import com.iberdrola.practicas2026.MarPG.data.dto.ElectronicInvoiceDto
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import org.junit.Assert.assertEquals
import org.junit.Test

class ElectronicInvoiceMapperTest {

    @Test
    fun `ElectronicInvoiceEntity toDomain mapea correctamente todos los campos`() {
        val entity = ElectronicInvoiceEntity(
            id = "123",
            type = "GAS",
            isEnabled = true,
            email = "test@iberdrola.es"
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(ContractType.GAS, domain.type)
        assertEquals(entity.isEnabled, domain.isEnabled)
        assertEquals(entity.email, domain.email)
    }

    @Test
    fun `toDomain debe usar LUZ por defecto si el tipo en la base de datos es invalido`() {
        val entity = ElectronicInvoiceEntity(
            id = "999",
            type = "CONTRATO_INVENTADO",
            isEnabled = false,
            email = ""
        )

        val domain = entity.toDomain()

        assertEquals(ContractType.LUZ, domain.type)
    }

    @Test
    fun `ElectronicInvoiceDto toEntity mapea correctamente desde el objeto de red`() {
        val dto = ElectronicInvoiceDto(
            id = "id_api",
            type = "LUZ",
            isEnabled = false,
            email = "api@test.com"
        )

        val entity = dto.toEntity()

        assertEquals(dto.id, entity.id)
        assertEquals(dto.type, entity.type)
        assertEquals(dto.isEnabled, entity.isEnabled)
    }

    @Test
    fun `ElectronicInvoice toEntity mapea correctamente el Enum a String`() {
        val domain = ElectronicInvoice(
            id = "dom_1",
            type = ContractType.GAS,
            isEnabled = true,
            email = "user@test.com"
        )

        val entity = domain.toEntity()

        assertEquals("GAS", entity.type)
        assertEquals(domain.id, entity.id)
    }
}