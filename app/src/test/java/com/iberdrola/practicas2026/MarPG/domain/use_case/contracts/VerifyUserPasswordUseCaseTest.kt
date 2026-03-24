package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState // <-- Importa el State de la UI
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VerifyUserPasswordUseCaseTest {

    private lateinit var useCase: VerifyUserPasswordUseCase
    private val userPrefs = mockk<UserPreferencesRepository>()

    @Before
    fun setUp() {
        useCase = VerifyUserPasswordUseCase(userPrefs)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `si la contraseña introducida coincide con la guardada debe devolver true`() = runTest {
        val mockState = ProfileState(password = "abcde")
        every { userPrefs.userProfileFlow } returns flowOf(mockState)

        val result = useCase("abcde")

        assertTrue(result)
    }

    @Test
    fun `si no hay contraseña guardada debe validar contra el valor por defecto 1234`() = runTest {
        val mockState = ProfileState(password = "")
        every { userPrefs.userProfileFlow } returns flowOf(mockState)

        val result = useCase("1234")

        assertTrue("Debería aceptar 1234 cuando el campo está vacío", result)
    }

    @Test
    fun `si la contraseña es incorrecta debe devolver false`() = runTest {
        val mockState = ProfileState(password = "contraseña_segura")
        every { userPrefs.userProfileFlow } returns flowOf(mockState)

        val result = useCase("password_incorrecto")

        assertFalse(result)
    }
}