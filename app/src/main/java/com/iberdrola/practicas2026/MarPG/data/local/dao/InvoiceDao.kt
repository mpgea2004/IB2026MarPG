package com.iberdrola.practicas2026.MarPG.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    /**
     * Devuelve todas las facturas
     */
    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    /**
     * Inserta la factura en la base de datos
     */
    @Insert
    suspend fun insertInvoice(factura: InvoiceEntity)

    /**
     * Elimina una factura de la base de datos
     */
    @Delete
    suspend fun deleteInvoice(factura: InvoiceEntity)
}