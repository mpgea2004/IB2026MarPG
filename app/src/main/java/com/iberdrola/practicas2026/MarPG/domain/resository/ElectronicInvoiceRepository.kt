package com.iberdrola.practicas2026.MarPG.domain.repository

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de contratos para la factura electrónica
 * Define el contrato para la obtención y actualización de datos
 */
interface ElectronicInvoiceRepository {
    /**
     * Obtiene todos los contratos en un flujo de datos
     * @param isCloud si es true, intenta sincronizar con la API (Mockoon)
     */
    fun getAllElectronicInvoice(isCloud: Boolean): Flow<List<ElectronicInvoice>>

    /**
     * Actualiza un contrato específico (para activar o editar el email)
     */
    suspend fun updateElectronicInvoice(electronicInvoice: ElectronicInvoice)
}