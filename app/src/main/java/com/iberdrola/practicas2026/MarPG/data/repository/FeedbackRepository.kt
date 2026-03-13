package com.iberdrola.practicas2026.MarPG.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor() {
    //Estas variables actúan como una especie de "base de datos" temporal
    var feedbackCount: Int = 0
    var feedbackTarget: Int = 1 //La tregua, qu puede ser 1, 3 o 10, dependiendo de lo que pulse el usuario
}