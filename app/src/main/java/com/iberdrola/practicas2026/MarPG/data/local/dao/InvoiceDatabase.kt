package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.data.local.entities.UserProfileEntity

/**
 * Base de datos principal de la aplicación utilizando Room
 * Define las entidades y versionado de la persistencia local
 */
@Database(
    version = 1,
    entities = [InvoiceEntity::class, ElectronicInvoiceEntity::class, UserProfileEntity::class,],
    exportSchema = false
)
abstract class InvoiceDatabase : RoomDatabase() {

    /**
     * Provee el DAO para realizar operaciones sobre la tabla de facturas
     */
    abstract fun invoiceDao(): InvoiceDao
    /**
     * Provee el DAO para realizar operaciones sobre la tabla de contratos
     */
    abstract fun electronicInvoiceDao(): ElectronicInvoiceDao

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: InvoiceDatabase? = null

        /**
         * Obtiene la instancia de la base de datos o la crea si no existe
         */
        fun getDatabase(context: Context): InvoiceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InvoiceDatabase::class.java,
                    "invoice_database.db"
                )
                    .fallbackToDestructiveMigration()
                    //Ya no añado el Callback de prepopulateDatabase para que empiece vacía
                    .build()
                INSTANCE = instance
                instance
            }
        }
        /*Esto lo comento porque ya no la uso
        private fun prepopulateDatabase(database: InvoiceDatabase) {
            val dao = database.invoiceDao()

            runBlocking {
                //Inserto un par de facturas de ejemplo
                dao.insertInvoice(
                    InvoiceEntity(
                        id = "FAC-001",
                        contractType = "LUZ",
                        amount = 45.50,
                        startDate = "01/01/2026",
                        endDate = "31/01/2026",
                        issueDate = "05/02/2026",
                        status = "PAID"
                    )
                )
                dao.insertInvoice(
                    InvoiceEntity(
                        id = "FAC-002",
                        contractType = "GAS",
                        amount = 120.30,
                        startDate = "01/12/2025",
                        endDate = "31/01/2026",
                        issueDate = "10/02/2026",
                        status = "PENDING"
                    )
                )
            }
        }
         */
    }
}