package com.iberdrola.practicas2026.MarPG.domain.use_case.events

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject

class LogAnalyticsEventUseCase @Inject constructor(
    private val analytics: FirebaseAnalytics
) {
    operator fun invoke(eventName: String, params: Map<String, Any?> = emptyMap()) {
        analytics.logEvent(eventName) {
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