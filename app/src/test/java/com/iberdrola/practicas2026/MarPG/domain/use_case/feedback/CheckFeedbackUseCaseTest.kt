package com.iberdrola.practicas2026.MarPG.domain.use_case.feedback

import com.iberdrola.practicas2026.MarPG.data.local.preferences.FeedbackDataStore
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckFeedbackUseCaseTest {

    private lateinit var useCase: CheckFeedbackUseCase
    private val dataStore = mockk<FeedbackDataStore>(relaxed = true)

    @Before
    fun setUp() {
        useCase = CheckFeedbackUseCase(dataStore)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `shouldShowFeedback debe ser true cuando los intentos restantes son 0 o menos`() = runTest {
        every { dataStore.remainingAttempts } returns flowOf(0)

        val result = useCase.shouldShowFeedback().first()

        assertTrue(result)
    }

    @Test
    fun `shouldShowFeedback debe ser false cuando aun quedan intentos positivos`() = runTest {
        every { dataStore.remainingAttempts } returns flowOf(3)

        val result = useCase.shouldShowFeedback().first()

        assertFalse(result)
    }

    @Test
    fun `notifyBackPress debe llamar a decrementar intentos en el DataStore`() = runTest {
        useCase.notifyBackPress()

        coVerify { dataStore.decrementAttempts() }
    }

    @Test
    fun `setNextTregua debe resetear el contador al valor indicado`() = runTest {
        val diasTregua = 10

        useCase.setNextTregua(diasTregua)

        coVerify { dataStore.resetTo(diasTregua) }
    }
}