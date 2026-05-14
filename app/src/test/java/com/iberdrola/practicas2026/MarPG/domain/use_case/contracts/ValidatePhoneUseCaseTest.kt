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
        assertTrue(useCase("600123456"))
        assertTrue(useCase("912345678"))
    }

    @Test
    fun `cuando el telefono no tiene 9 digitos debe ser invalido`() {
        assertFalse(useCase("60012345"))
        assertFalse(useCase("6001234567"))
        assertFalse(useCase(""))
    }

    @Test
    fun `cuando el telefono contiene caracteres no numericos debe ser invalido`() {
        assertFalse(useCase("60012345a"))
        assertFalse(useCase("600-12345"))
        assertFalse(useCase(" 600123456"))
    }
}
