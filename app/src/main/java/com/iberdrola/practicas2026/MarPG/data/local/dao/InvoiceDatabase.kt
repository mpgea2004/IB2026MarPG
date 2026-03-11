package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@Database(
    version = 1,
    entities = [InvoiceEntity::class],
    exportSchema = false
)
abstract class InvoiceDatabase : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao

    companion object {
        @Volatile
        private var INSTANCE: InvoiceDatabase? = null

        fun getDatabase(context: Context): InvoiceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InvoiceDatabase::class.java,
                    "invoice_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                INSTANCE?.let { database ->
                                    prepopulateDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

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
    }
}