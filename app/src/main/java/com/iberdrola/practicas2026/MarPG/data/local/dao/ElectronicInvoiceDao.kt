package com.iberdrola.practicas2026.MarPG.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectronicInvoiceDao {
    @Query("SELECT * FROM electronic_invoices")
    fun getAllElectronicInvoice(): Flow<List<ElectronicInvoiceEntity>>

    /**
     * Devuelve todas las fact elect una sola vez, sin flujo (para el fallback cuando falla la red)
     */
    @Query("SELECT * FROM electronic_invoices")
    suspend fun getAllElectronicInvoiceOnce(): List<ElectronicInvoiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ElectronicInvoice: List<ElectronicInvoiceEntity>)

    @Update
    suspend fun updateElectronicInvoice(ElectronicInvoice: ElectronicInvoiceEntity)
}