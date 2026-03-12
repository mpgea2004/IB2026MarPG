package com.iberdrola.practicas2026.MarPG.data.network

/**
 * Clase sellada que utilizo como envoltorio (wrapper) para los resultados de mis operaciones.
 * La he diseñado así para gestionar de forma elegante el éxito o el fallo de cualquier
 * acción en mi app
 * * Al ser 'out T', me permite trabajar con cualquier tipo de dato de forma genérica.
 */
sealed class BaseResult<out T> {

    /**
     * Representa un resultado satisfactorio.
     * Contiene el objeto genérico con la información que necesito devolver.
     * * @param data Los datos obtenidos
     */
    data class Success<T>(var data: T): BaseResult<T>()

    /**
     * Representa un fallo en la operación.
     * En lugar de dejar que la app lance una excepción y se cierre, capturo el error aquí.
     * * @param exception La excepción o error personalizado que ha ocurrido.
     */
    data class Error(var exception: Exception): BaseResult<Nothing>()
}