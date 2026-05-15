package com.iberdrola.practicas2026.MarPG.domain.use_case.events

import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsManager
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import javax.inject.Inject

class LogAnalyticsEventUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
    operator fun invoke(
        eventName: String, 
        params: Map<String, Any?> = emptyMap(),
        priority: AnalyticsPriority = AnalyticsPriority.MEDIUM
    ) {
        analyticsManager.logEvent(eventName, params, priority)
    }
}