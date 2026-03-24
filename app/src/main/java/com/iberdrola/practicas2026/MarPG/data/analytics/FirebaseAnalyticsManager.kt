package com.iberdrola.practicas2026.MarPG.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsManager
import javax.inject.Inject

class FirebaseAnalyticsManager @Inject constructor(
    private val firebase: FirebaseAnalytics
) : AnalyticsManager {

    override fun logEvent(name: String, params: Map<String, Any?>) {
        firebase.logEvent(name) {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Boolean -> param(key, if (value) "true" else "false")
                    else -> param(key, value.toString())
                }
            }
        }
    }
}