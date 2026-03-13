package com.iberdrola.practicas2026.MarPG.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utilidad para la gestión y transformación de formatos de fecha
 * Centraliza la lógica de parseo para ordenación y formateo para visualización
 */
object DateMapper {
    /** Formato de entrada esperado de los datos (dd/MM/yyyy) */
    private val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /** Formato de salida para la interfaz de usuario (Ej: 4 de marzo) */
    private val outputFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))

    /**
     * Convierte un String de fecha en un objeto [LocalDate]
     * @param dateString Fecha en formato "dd/MM/yyyy"
     * @return Objeto [LocalDate] o la fecha actual si ocurre un error de parseo.
     */
    fun toLocalDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, inputFormatter)
        } catch (e: Exception) {
            //Si el formato falla, devuelvo hoy
            LocalDate.now()
        }
    }

    /**
     * Transforma una fecha de formato técnico a formato amigable para el usuario
     * @param dateString Fecha en formato "dd/MM/yyyy"
     * @return String formateado (ej. "12 de enero") o el original si falla.
     */
    fun formatToDisplay(dateString: String): String {
        return try {
            val date = toLocalDate(dateString)
            date.format(outputFormatter)
        } catch (e: Exception) {
            dateString //Devuelvo el original si hay error
        }
    }

}