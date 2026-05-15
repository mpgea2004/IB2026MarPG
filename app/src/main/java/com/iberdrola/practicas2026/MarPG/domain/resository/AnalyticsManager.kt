package com.iberdrola.practicas2026.MarPG.domain.resository

enum class AnalyticsPriority {
    LOW, MEDIUM, HIGH
}

interface AnalyticsManager {
    fun logEvent(
        name: String, 
        params: Map<String, Any?> = emptyMap(), 
        priority: AnalyticsPriority = AnalyticsPriority.MEDIUM
    )
}