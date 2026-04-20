package com.iberdrola.practicas2026.MarPG.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de acceso a datos (DAO) para la entidad [InvoiceEntity]
 */
@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices")
    suspend fun getAllInvoicesOnce(): List<InvoiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)

    @Query("DELETE FROM invoices")
    suspend fun clearCache()

    @Transaction
    suspend fun refreshCache(invoices: List<InvoiceEntity>) {
        clearCache()
        insertInvoices(invoices)
    }

    @Query("UPDATE invoices SET status = 'PAGADAS' WHERE id = :id")
    suspend fun updateInvoiceToPaid(id: String)
}