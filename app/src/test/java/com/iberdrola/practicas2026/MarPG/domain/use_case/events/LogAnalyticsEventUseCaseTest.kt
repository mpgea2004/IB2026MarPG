package com.iberdrola.practicas2026.MarPG.domain.use_case.events

import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsManager
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class LogAnalyticsEventUseCaseTest {

    private lateinit var useCase: LogAnalyticsEventUseCase
    private val analyticsManager = mockk<AnalyticsManager>(relaxed = true)

    @Before
    fun setUp() {
        useCase = LogAnalyticsEventUseCase(analyticsManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `cuando se invoca el caso de uso se debe delegar la llamada al manager`() {
        // GIVEN
        val eventName = "screen_view"
        val params = mapOf("screen_name" to "Main")

        // WHEN
        useCase(eventName, params)

        // THEN: Verificamos que nuestro manager recibe los datos
        verify { analyticsManager.logEvent(eventName, params) }
    }

    @Test
    fun `si no se pasan parametros debe enviar un mapa vacio por defecto`() {
        // GIVEN
        val eventName = "app_open"

        // WHEN
        useCase(eventName)

        // THEN
        verify { analyticsManager.logEvent(eventName, emptyMap()) }
    }
}