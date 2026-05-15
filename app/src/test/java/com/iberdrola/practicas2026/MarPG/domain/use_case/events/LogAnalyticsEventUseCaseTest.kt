package com.iberdrola.practicas2026.MarPG.domain.use_case.events

import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsManager
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
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
    fun `cuando se invoca el caso de uso se debe delegar la llamada al manager con prioridad media por defecto`() {
        val eventName = "screen_view"
        val params = mapOf("screen_name" to "Main")

        useCase(eventName, params)

        verify { analyticsManager.logEvent(eventName, params, AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `si no se pasan parametros debe enviar un mapa vacio por defecto`() {
        val eventName = "app_open"

        useCase(eventName)

        verify { analyticsManager.logEvent(eventName, emptyMap(), AnalyticsPriority.MEDIUM) }
    }

    @Test
    fun `debe respetar la prioridad pasada por parametro`() {
        val eventName = "critical_error"
        
        useCase(eventName, priority = AnalyticsPriority.HIGH)

        verify { analyticsManager.logEvent(eventName, emptyMap(), AnalyticsPriority.HIGH) }
    }
}
