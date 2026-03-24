package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest {

    private lateinit var useCase: ValidateEmailUseCase

    @Before
    fun setUp() {
        useCase = ValidateEmailUseCase()
    }

    @Test
    fun `cuando el email tiene formato correcto debe devolver true`() {
        val email1 = "test@iberdrola.es"
        val email2 = "usuario.nombre@gmail.com"
        val email3 = "apps_2026@empresa.org"

        assertTrue(useCase(email1))
        assertTrue(useCase(email2))
        assertTrue(useCase(email3))
    }

    @Test
    fun `cuando el email no tiene arroba debe devolver false`() {
        val email = "testiberdrola.es"

        assertFalse("Debe fallar si falta la arroba", useCase(email))
    }

    @Test
    fun `cuando el email no tiene dominio o extension debe devolver false`() {
        val noExtension = "test@iberdrola"
        val noDominio = "test@.es"

        assertFalse(useCase(noExtension))
        assertFalse(useCase(noDominio))
    }

    @Test
    fun `debe ignorar espacios en blanco al principio o al final`() {
        val emailConEspacios = "  test@iberdrola.es  "

        assertTrue("El trim() debería limpiar los espacios", useCase(emailConEspacios))
    }

    @Test
    fun `cuando el string esta vacio o solo tiene espacios debe devolver false`() {
        val empty = ""
        val blank = "   "

        assertFalse(useCase(empty))
        assertFalse(useCase(blank))
    }

    @Test
    fun `cuando el dominio tiene menos de dos letras de extension debe devolver false`() {
        val emailMalo = "test@mail.x"

        assertFalse("La extensión debe tener al menos 2 letras (es, com, org...)", useCase(emailMalo))
    }
}