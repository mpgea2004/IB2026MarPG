package com.iberdrola.practicas2026.MarPG.domain.resository

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de facturas
 * Define el contrato para la obtención de datos, abstrayendo la fuente de información
 */
interface InvoiceRepository {
    /**
     * Me devuelve todas las facturas en un flujo de datos
     * @param isCloud si es true, usa la api(Mockoon), y si es falso usa el json que tengo en assets
     */
    fun getAllInvoices(isCloud: Boolean): Flow<List<Invoice>>
}