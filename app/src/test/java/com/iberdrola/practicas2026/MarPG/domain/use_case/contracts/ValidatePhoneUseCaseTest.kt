package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidatePhoneUseCaseTest {

    private lateinit var useCase: ValidatePhoneUseCase

    @Before
    fun setUp() {
        useCase = ValidatePhoneUseCase()
    }

    @Test
    fun `cuando el telefono tiene 9 digitos exactos debe ser valido`() {
        val phone = "600123456"

        val result = useCase(phone)

        assertTrue("El teléfono de 9 dígitos debería ser válido", result)
    }

    @Test
    fun `cuando el telefono tiene menos de 9 digitos debe ser invalido`() {
        val phone = "60012345"

        val result = useCase(phone)

        assertFalse("Menos de 9 dígitos no debe permitirse", result)
    }

    @Test
    fun `cuando el telefono tiene mas de 9 digitos debe ser invalido`() {
        val phone = "6001234567"

        val result = useCase(phone)

        assertFalse("Más de 9 dígitos no debe permitirse", result)
    }

    @Test
    fun `cuando el telefono contiene letras debe ser invalido`() {
        val phone = "60012345a"

        val result = useCase(phone)

        assertFalse("No se deben permitir letras en el teléfono", result)
    }

    @Test
    fun `cuando el string esta vacio debe ser invalido`() {
        val phone = ""
        val result = useCase(phone)

        assertFalse("Un string vacío no es un teléfono válido", result)
    }
}