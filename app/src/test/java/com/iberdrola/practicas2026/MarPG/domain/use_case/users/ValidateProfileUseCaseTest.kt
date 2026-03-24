package com.iberdrola.practicas2026.MarPG.domain.use_case.users

import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidateEmailUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.ValidatePhoneUseCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateProfileUseCaseTest {

    private lateinit var useCase: ValidateProfileUseCase
    private val validateEmail = mockk<ValidateEmailUseCase>()
    private val validatePhone = mockk<ValidatePhoneUseCase>()

    @Before
    fun setUp() {
        useCase = ValidateProfileUseCase(validateEmail, validatePhone)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando el email y el telefono son correctos debe devolver Success`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true

        val result = useCase("test@iberdrola.es", "600123456")

        assertTrue(result is ProfileValidationResult.Success)
    }

    @Test
    fun `si el email falla debe devolver un objeto Error con el mensaje de email`() {
        every { validateEmail(any()) } returns false
        every { validatePhone(any()) } returns true

        val result = useCase("email_malo", "600123456")

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals("El formato del correo no es válido", errorResult.emailError)
        assertEquals(null, errorResult.phoneError)
    }

    @Test
    fun `si el telefono falla debe devolver un objeto Error con el mensaje de telefono`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns false

        val result = useCase("test@iberdrola.es", "123")

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(null, errorResult.emailError)
        assertEquals("El teléfono debe tener 9 dígitos", errorResult.phoneError)
    }

    @Test
    fun `si ambos campos estan vacios debe permitirlo como Success`() {
        val result = useCase("", "")

        assertTrue(result is ProfileValidationResult.Success)
    }

    @Test
    fun `si ambos fallan debe devolver ambos mensajes de error`() {
        every { validateEmail(any()) } returns false
        every { validatePhone(any()) } returns false

        val result = useCase("error", "error")

        val errorResult = result as ProfileValidationResult.Error
        assertEquals("El formato del correo no es válido", errorResult.emailError)
        assertEquals("El teléfono debe tener 9 dígitos", errorResult.phoneError)
    }
}