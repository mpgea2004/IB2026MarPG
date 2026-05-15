package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import com.iberdrola.practicas2026.MarPG.data.local.entities.UserProfileEntity

@Database(
    version = 1,
    entities = [InvoiceEntity::class, ElectronicInvoiceEntity::class, UserProfileEntity::class,],
    exportSchema = false
)
abstract class InvoiceDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao
    abstract fun electronicInvoiceDao(): ElectronicInvoiceDao

    abstract fun userDao(): UserDao

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}