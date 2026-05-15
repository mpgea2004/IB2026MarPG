package com.iberdrola.practicas2026.MarPG.data.network

sealed class InvoiceException(message: String) : Exception(message) {
    data object NetworkError : InvoiceException("Error de red")
    data class ServerError(val code: Int) : InvoiceException("Error de servidor: $code")
    data object NotFoundError : InvoiceException("No encontrado")
    data object LocalDataError : InvoiceException("Error en datos locales")
    data object Unknown : InvoiceException("Error desconocido")
}