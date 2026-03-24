package com.iberdrola.practicas2026.MarPG.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iberdrola.practicas2026.MarPG.data.local.entities.UserProfileEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var db: InvoiceDatabase
    private lateinit var dao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, InvoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun createMockUser(id: Int = 0, name: String = "Test", email: String = "test@test.com") =
        UserProfileEntity(
            id = id,
            name = name,
            email = email,
            phoneNumber = "600000000",
            address = "Calle Falsa 123",
            password = "password123"
        )

    @Test
    fun getUser_WhenDatabaseIsEmpty_ShouldReturnNull() = runTest {
        val result = dao.getUser()

        assertNull("Debería ser null si no hay usuario guardado", result)
    }

    @Test
    fun saveUser_AndGet_ShouldReturnCorrectUser() = runTest {
        val user = createMockUser(name = "Mario", email = "mario@iberdrola.es")

        dao.saveUser(user)
        val result = dao.getUser()

        assertEquals("Mario", result?.name)
        assertEquals(0, result?.id)
        assertEquals("mario@iberdrola.es", result?.email)
    }

    @Test
    fun saveUser_OnConflict_ShouldReplaceOldUser() = runTest {
        val oldUser = createMockUser(name = "Viejo", email = "old@test.com")
        dao.saveUser(oldUser)

        val newUser = createMockUser(name = "Nuevo", email = "new@test.com")
        dao.saveUser(newUser)

        val result = dao.getUser()
        assertEquals("Nuevo", result?.name)
        assertEquals("new@test.com", result?.email)
    }
}