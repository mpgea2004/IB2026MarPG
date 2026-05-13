package com.iberdrola.practicas2026.MarPG.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateMapper {
    private val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
    private val shortOutputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
    fun toLocalDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, inputFormatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun formatToDisplay(dateString: String): String {
        return try {
            val date = toLocalDate(dateString)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatToShortDisplay(dateString: String): String {
        return try {
            val date = toLocalDate(dateString)

            var formatted = date.format(shortOutputFormatter).lowercase()
            formatted = formatted.replace(".", "")
            formatted = formatted.replace(" ([a-z]{3}) ".toRegex(), " $1. ")
            formatted
        } catch (e: Exception) {
            dateString
        }
    }

}