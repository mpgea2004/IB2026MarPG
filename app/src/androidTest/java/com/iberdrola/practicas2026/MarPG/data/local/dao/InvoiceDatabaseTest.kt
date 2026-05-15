package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
    fun database_ShouldProvideAllDaosCorectly() {
        assertNotNull("InvoiceDao no deberia ser nulo", db.invoiceDao())
        assertNotNull("ElectronicInvoiceDao no debería ser nulo", db.electronicInvoiceDao())
        assertNotNull("UserDao no debería ser nulo", db.userDao())
    }

    @Test
    fun database_IsSingletonViaCompanionObject() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val instance1 = InvoiceDatabase.getDatabase(context)
        val instance2 = InvoiceDatabase.getDatabase(context)

        assertTrue("La base de datos debería ser un Singleton", instance1 === instance2)

        instance1.close()
    }
}
