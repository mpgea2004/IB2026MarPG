package com.iberdrola.practicas2026.MarPG.data.network

/**
 * Clase sellada para gestionar los errores específicos de las facturas.
 * Centraliza los fallos de red, servidor y base de datos para mostrar
 * mensajes claros en la interfaz de Iberdrola.
 */
sealed class InvoiceException(message: String) : Exception(message) {
    data object NetworkError : InvoiceException("Error de red")
    data class ServerError(val code: Int) : InvoiceException("Error de servidor: $code")
    data object NotFoundError : InvoiceException("No encontrado")
    data object LocalDataError : InvoiceException("Error en datos locales")
    data object Unknown : InvoiceException("Error desconocido")
}