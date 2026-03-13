package com.iberdrola.practicas2026.MarPG.data.repository

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio de persistencia en memoria para el sistema de feedback
 * Al ser [Singleton], mantiene el estado del contador durante el ciclo de vida de la app
 */
@Singleton
class FeedbackRepository @Inject constructor() {
    /** Contador de interacciones del usuario */
    var feedbackCount: Int = 0
    /** Objetivo de interacciones (tregua) para mostrar el feedback (1, 3 o 10) */
    var feedbackTarget: Int = 1
}