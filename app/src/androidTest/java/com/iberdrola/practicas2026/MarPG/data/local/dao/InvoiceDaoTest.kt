package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.MarPG.data.local.entities.InvoiceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InvoiceDaoTest {
    private lateinit var db: InvoiceDatabase
    private lateinit var dao: InvoiceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InvoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.invoiceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun createMockInvoice(id: String, amount: Double) = InvoiceEntity(
        id = id,
        amount = amount,
        contractType = "LUZ",
        startDate = "01/01/2026",
        endDate = "31/01/2026",
        issueDate = "05/02/2026",
        status = "PAID"
    )

    @Test
    fun insertAndGetAllInvoices_ShouldReturnCorrectData() = runTest {
        val invoices = listOf(
            createMockInvoice("FAC-1", 45.0),
            createMockInvoice("FAC-2", 30.0)
        )

        dao.insertInvoices(invoices)

        val result = dao.getAllInvoices().first()
        assertEquals(2, result.size)
        assertEquals("FAC-1", result[0].id)
    }

    @Test
    fun clearCache_ShouldLeaveTableEmpty() = runTest {
        dao.insertInvoices(listOf(createMockInvoice("1",  10.0)))

        dao.clearCache()

        val result = dao.getAllInvoicesOnce()
        assertTrue("La lista debería estar vacía tras clearCache", result.isEmpty())
    }

    @Test
    fun refreshCache_ShouldReplaceOldDataWithNewData() = runTest {
        val oldData = listOf(createMockInvoice("OLD",  1.0))
        dao.insertInvoices(oldData)

        val newData = listOf(
            createMockInvoice("NEW-1", 100.0),
            createMockInvoice("NEW-2",  200.0)
        )
        dao.refreshCache(newData)

        val result = dao.getAllInvoicesOnce()
        assertEquals(2, result.size)
        assertTrue("El registro OLD debería haber sido borrado", result.none { it.id == "OLD" })
        assertEquals("NEW-1", result[0].id)
    }
}