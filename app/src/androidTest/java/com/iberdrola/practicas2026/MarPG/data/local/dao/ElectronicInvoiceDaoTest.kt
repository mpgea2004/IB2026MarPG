package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ElectronicInvoiceDaoTest {
    private lateinit var db: InvoiceDatabase
    private lateinit var dao: ElectronicInvoiceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InvoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.electronicInvoiceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAllAndGetAll_ShouldReturnCorrectData() = runTest {
        val entities = listOf(
            ElectronicInvoiceEntity(id = "1", type = "LUZ", isEnabled = true, email = "luz@test.com"),
            ElectronicInvoiceEntity(id = "2", type = "GAS", isEnabled = false, email = "")
        )

        dao.insertAll(entities)
        val result = dao.getAllElectronicInvoiceOnce() // Probamos la función suspend

        assertEquals(2, result.size)
        assertEquals("luz@test.com", result[0].email)
        assertEquals("GAS", result[1].type)
    }

    @Test
    fun updateElectronicInvoice_ShouldModifyExistingRecord() = runTest {
        val initial = ElectronicInvoiceEntity(id = "1", type = "LUZ", isEnabled = false, email = "")
        dao.insertAll(listOf(initial))

        val updated = initial.copy(isEnabled = true, email = "nuevo@iberdrola.es")
        dao.updateElectronicInvoice(updated)

        val result = dao.getAllElectronicInvoiceOnce()
        assertEquals(1, result.size)
        assertTrue(result[0].isEnabled)
        assertEquals("nuevo@iberdrola.es", result[0].email)
    }

    @Test
    fun flow_ShouldEmitNewData_WhenDatabaseIsUpdated() = runTest {
        val entities = listOf(ElectronicInvoiceEntity(id = "1", type = "LUZ", isEnabled = true, email = "test@test.com"))

        dao.insertAll(entities)

        val result = dao.getAllElectronicInvoice().first()
        assertEquals(1, result.size)
        assertEquals("test@test.com", result[0].email)
    }
}