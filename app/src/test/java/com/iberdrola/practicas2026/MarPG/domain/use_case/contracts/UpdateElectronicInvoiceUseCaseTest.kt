package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class UpdateElectronicInvoiceUseCaseTest {

    private lateinit var useCase: UpdateElectronicInvoiceUseCase
    private val repository = mockk<ElectronicInvoiceRepository>(relaxed = true)

    @Before
    fun setUp() {
        useCase = UpdateElectronicInvoiceUseCase(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `debe llamar al repositorio con el objeto de factura electronica correcto`() = runTest {
        val mockInvoice = ElectronicInvoice(
            id = "test_id",
            type = ContractType.LUZ,
            isEnabled = true,
            email = "test@iberdrola.es"
        )

        useCase(mockInvoice)

        coVerify(exactly = 1) { repository.updateElectronicInvoice(mockInvoice) }
    }

    @Test
    fun `debe propagar la excepcion si el repositorio falla`() = runTest {
        val mockInvoice = ElectronicInvoice("id", ContractType.GAS, false, "")
        coEvery { repository.updateElectronicInvoice(any()) } throws Exception("Error de conexión")

        try {
            useCase(mockInvoice)
            assert(false) { "Debería haber lanzado una excepción" }
        } catch (e: Exception) {
            assert(e.message == "Error de conexión")
        }
    }
}