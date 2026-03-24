package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FormatUserPhoneUseCaseTest {

    private lateinit var useCase: FormatUserPhoneUseCase

    @Before
    fun setUp() {
        useCase = FormatUserPhoneUseCase()
    }

    @Test
    fun `cuando el telefono es valido debe mostrar asteriscos y los 3 ultimos digitos`() {
        val phone = "600123456"

        val result = useCase(phone)

        assertEquals("******456", result)
    }

    @Test
    fun `cuando el telefono tiene exactamente 3 digitos debe mostrar asteriscos y los 3 digitos`() {
        val phone = "123"

        val result = useCase(phone)

        assertEquals("******123", result)
    }

    @Test
    fun `cuando el telefono es muy corto menos de 3 digitos debe mostrar solo asteriscos`() {
        val phone = "12"

        val result = useCase(phone)

        assertEquals("******", result)
    }

    @Test
    fun `cuando el string esta vacio debe mostrar solo asteriscos`() {
        val phone = ""
        val result = useCase(phone)

        assertEquals("******", result)
    }

    @Test
    fun `debe funcionar correctamente con numeros que contienen prefijos si los hubiera`() {
        val phone = "+34600111222"

        val result = useCase(phone)
        assertEquals("******222", result)
    }
}