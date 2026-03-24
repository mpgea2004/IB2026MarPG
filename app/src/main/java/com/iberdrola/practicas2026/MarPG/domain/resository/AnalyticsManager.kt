package com.iberdrola.practicas2026.MarPG.domain.resository

interface AnalyticsManager {
    fun logEvent(name: String, params: Map<String, Any?>)
}