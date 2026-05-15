package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.MarPG.data.local.entities.ElectronicInvoiceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    fun insertAllAndGetAll_ShouldReturnCorrectData() = runBlocking {
        val entities = listOf(
            ElectronicInvoiceEntity(id = "1", type = "LUZ", isEnabled = true, email = "luz@test.com"),
            ElectronicInvoiceEntity(id = "2", type = "GAS", isEnabled = false, email = "")
        )

        dao.insertAll(entities)
        val result = dao.getAllElectronicInvoiceOnce()

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "1" && it.type == "LUZ" && it.email == "luz@test.com" })
        assertTrue(result.any { it.id == "2" && it.type == "GAS" && it.isEnabled == false })
    }

    @Test
    fun updateElectronicInvoice_ShouldModifyExistingRecord() = runBlocking {
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
    fun flow_ShouldEmitNewData_WhenDatabaseIsUpdated() = runBlocking {
        val entities = listOf(ElectronicInvoiceEntity(id = "1", type = "LUZ", isEnabled = true, email = "test@test.com"))

        dao.insertAll(entities)

        val firstEmission = dao.getAllElectronicInvoice().first()
        assertEquals(1, firstEmission.size)

        val newEntity = ElectronicInvoiceEntity(id = "2", type = "GAS", isEnabled = true, email = "gas@test.com")
        dao.insertAll(listOf(newEntity))

        val secondEmission = dao.getAllElectronicInvoice().first()
        assertEquals(2, secondEmission.size)
    }
}
