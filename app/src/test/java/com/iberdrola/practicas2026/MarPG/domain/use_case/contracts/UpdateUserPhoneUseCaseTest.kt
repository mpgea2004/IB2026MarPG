package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateUserPhoneUseCaseTest {

    private lateinit var useCase: UpdateUserPhoneUseCase
    private val userPrefs = mockk<UserPreferencesRepository>(relaxed = true)
    private val validatePhone = mockk<ValidatePhoneUseCase>()

    @Before
    fun setUp() {
        useCase = UpdateUserPhoneUseCase(userPrefs, validatePhone)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando el telefono es valido debe guardarlo y devolver true`() = runTest {
        val phone = "600123456"
        every { validatePhone(phone) } returns true

        val result = useCase(phone)

        assertTrue("Debe devolver true al ser válido", result)
        coVerify { userPrefs.updatePhone(phone) }
    }

    @Test
    fun `cuando el telefono es invalido no debe guardar nada y devolver false`() = runTest {
        val phone = "123"
        every { validatePhone(phone) } returns false

        val result = useCase(phone)

        assertFalse("Debe devolver false al ser inválido", result)
        coVerify(exactly = 0) { userPrefs.updatePhone(any()) }
    }
}