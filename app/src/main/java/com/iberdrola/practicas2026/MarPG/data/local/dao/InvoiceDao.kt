package com.iberdrola.practicas2026.MarPG.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
     * Devuelve todas las facturas una sola vez, sin flujo (para el fallback cuando falla la red)
     */
    @Query("SELECT * FROM invoices")
    suspend fun getAllInvoicesOnce(): List<InvoiceEntity>

    /**
     * Inserta las facturas en la bd (si ya existen, las reemplaza con los datos nuevos, lo pongo por si activo y desactivo ms de una vez el swich de nube/local)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)

    /**
     * Limpia la caché
     */
    @Query("DELETE FROM invoices")
    suspend fun clearCache()

    /**
     * Inserta la factura en la base de datos, no la usamos asique está comentada, pero por si acaso lo implementamos luego, antes la usaba para cargar datos en la bd con el prepopulateDatabase
     */
    /*
    @Insert
    suspend fun insertInvoice(factura: InvoiceEntity)
    */

    /**
     * Elimina una factura de la base de datos, no la usamos asique está comentada, pero por si acaso lo implementamos luego
     */
    /*
    @Delete
    suspend fun deleteInvoice(factura: InvoiceEntity)
    */
}