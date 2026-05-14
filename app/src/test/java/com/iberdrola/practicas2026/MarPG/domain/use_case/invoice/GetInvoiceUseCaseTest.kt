package com.iberdrola.practicas2026.MarPG.domain.use_case.invoice

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetInvoiceUseCaseTest {

    private lateinit var useCase: GetInvoiceUseCase
    private val repository = mockk<InvoiceRepository>()

    @Before
    fun setUp() {
        useCase = GetInvoiceUseCase(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `debe llamar al repositorio con el parametro isCloud correcto`() = runTest {
        every { repository.getAllInvoices(any()) } returns flowOf(emptyList())

        useCase(isCloud = true).first()
        verify { repository.getAllInvoices(true) }

        useCase(isCloud = false).first()
        verify { repository.getAllInvoices(false) }
    }

    @Test
    fun `debe devolver las facturas ordenadas por fecha de forma descendente`() = runTest {
        val invoices = listOf(
            createInvoice("1", "01/01/2023"),
            createInvoice("2", "20/05/2024"),
            createInvoice("3", "15/02/2024")
        )
        every { repository.getAllInvoices(any()) } returns flowOf(invoices)

        val result = useCase(isCloud = true).first()

        assertEquals(3, result.size)
        assertEquals("20/05/2024", result[0].issueDate)
        assertEquals("15/02/2024", result[1].issueDate)
        assertEquals("01/01/2023", result[2].issueDate)
    }

    @Test
    fun `si el repositorio devuelve una lista vacia debe retornar lista vacia`() = runTest {
        every { repository.getAllInvoices(any()) } returns flowOf(emptyList())

        val result = useCase(isCloud = true).first()

        assertTrue(result.isEmpty())
    }

    private fun createInvoice(id: String, issueDate: String) = Invoice(
        id = id,
        contractType = ContractType.LUZ,
        amount = 100.0,
        startDate = "01/01/2020",
        endDate = "01/02/2020",
        issueDate = issueDate,
        status = InvoiceStatus.PAGADAS
    )
}
