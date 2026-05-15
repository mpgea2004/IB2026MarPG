package com.iberdrola.practicas2026.MarPG.domain.use_case.users

import com.iberdrola.practicas2026.MarPG.R
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
    private val validateEmail = mockk<ValidateEmailUseCase>(relaxed = true)
    private val validatePhone = mockk<ValidatePhoneUseCase>(relaxed = true)

    @Before
    fun setUp() {
        useCase = ValidateProfileUseCase(validateEmail, validatePhone)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando todos los datos son validos debe devolver Success`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true

        val result = useCase(
            name = "Mar",
            email = "test@iberdrola.es",
            phone = "600123456",
            password = "password123",
            confirmPassword = "password123"
        )

        assertTrue(result is ProfileValidationResult.Success)
    }

    @Test
    fun `si el nombre esta vacio debe devolver error de campo`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true

        val result = useCase(
            name = "",
            email = "test@iberdrola.es",
            phone = "600123456",
            password = "password123",
            confirmPassword = "password123"
        )

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(R.string.error_field_required, errorResult.nameError)
    }

    @Test
    fun `si el email falla debe devolver un objeto Error con el mensaje de formato invalido`() {
        every { validateEmail(any()) } returns false
        every { validatePhone(any()) } returns true

        val result = useCase(
            name = "Mar",
            email = "email_malo",
            phone = "600123456",
            password = "password123",
            confirmPassword = "password123"
        )

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(R.string.error_invalid_email_format, errorResult.emailError)
    }

    @Test
    fun `si las contraseñas no coinciden debe devolver error`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true
        
        val result = useCase(
            name = "Mar",
            email = "test@test.com",
            phone = "600123456",
            password = "password123",
            confirmPassword = "password456"
        )

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(R.string.error_passwords_do_not_match, errorResult.confirmPasswordError)
    }

    @Test
    fun `si el telefono no es valido debe devolver error de formato de telefono`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns false

        val result = useCase(
            name = "Mar",
            email = "test@test.com",
            phone = "123",
            password = "password123",
            confirmPassword = "password123"
        )

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(R.string.error_invalid_phone_format, errorResult.phoneError)
    }

    @Test
    fun `si la contraseña es demasiado corta debe devolver error correspondiente`() {
        every { validateEmail(any()) } returns true
        every { validatePhone(any()) } returns true

        val result = useCase(
            name = "Mar",
            email = "test@test.com",
            phone = "600123456",
            password = "123",
            confirmPassword = "123"
        )

        assertTrue(result is ProfileValidationResult.Error)
        val errorResult = result as ProfileValidationResult.Error
        assertEquals(R.string.error_password_too_short, errorResult.passwordError)
    }
}
