package com.iberdrola.practicas2026.MarPG.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateMapper {
    //LEER: dd/MM/yyyy
    private val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    //MOSTRAR: d 'de' MMMM (Ej: 4 de marzo)
    private val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

    fun toLocalDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, inputFormatter)
        } catch (e: Exception) {
            //Si el formato falla, devuelvo hoy
            LocalDate.now()
        }
    }

    fun formatToDisplay(dateString: String): String {
        return try {
            val date = toLocalDate(dateString)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString //Devuelvo el original si hay error
        }
    }

}