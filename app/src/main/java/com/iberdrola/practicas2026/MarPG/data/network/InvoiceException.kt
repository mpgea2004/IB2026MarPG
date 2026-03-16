package com.iberdrola.practicas2026.MarPG.data.network

/**
 * Clase sellada para gestionar los errores específicos de las facturas.
 * Centraliza los fallos de red, servidor y base de datos para mostrar
 * mensajes claros en la interfaz de Iberdrola.
 */
sealed class InvoiceException(message: String) : Exception(message) {
    data object NetworkError : InvoiceException("Error al comunicarse con el servidor, revise su red")
    data class ServerError(val code: Int) : InvoiceException("Error en el servidor de Iberdrola (Código: $code)")
    data object LocalDataError : InvoiceException("No se han podido cargar las facturas locales.")
    data object Unknown : InvoiceException("Ha ocurrido un error inesperado.")
}