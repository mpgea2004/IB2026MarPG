package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InvoiceDatabaseTest {
    private lateinit var db: InvoiceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InvoiceDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun database_ShouldProvideAllDaosCorrectly() {
        val invoiceDao = db.invoiceDao()
        val electronicInvoiceDao = db.electronicInvoiceDao()
        val userDao = db.userDao()

        assertNotNull("InvoiceDao no debería ser nulo", invoiceDao)
        assertNotNull("ElectronicInvoiceDao no debería ser nulo", electronicInvoiceDao)
        assertNotNull("UserDao no debería ser nulo", userDao)
    }

    @Test
    fun database_IsSingletonViaCompanionObject() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val instance1 = InvoiceDatabase.getDatabase(context)
        val instance2 = InvoiceDatabase.getDatabase(context)

        assert(instance1 === instance2)

        instance1.close()
    }
}