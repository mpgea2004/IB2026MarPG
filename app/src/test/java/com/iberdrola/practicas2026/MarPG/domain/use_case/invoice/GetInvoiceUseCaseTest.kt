package com.iberdrola.practicas2026.MarPG.domain.use_case.invoice

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
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
    private val invoiceAntigua = mockk<Invoice> {
        every { issueDate } returns "01/01/2023"
    }
    private val invoiceNueva = mockk<Invoice> {
        every { issueDate } returns "20/05/2024"
    }
    private val invoiceMedia = mockk<Invoice> {
        every { issueDate } returns "15/02/2024"
    }

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
        every { repository.getAllInvoices(true) } returns flowOf(emptyList())
        useCase(isCloud = true).first()
        verify { repository.getAllInvoices(true) }
    }

    @Test
    fun `debe devolver las facturas ordenadas por fecha de forma descendente`() = runTest {
        val listaDesordenada = listOf(invoiceAntigua, invoiceNueva, invoiceMedia)
        every { repository.getAllInvoices(any()) } returns flowOf(listaDesordenada)

        val result = useCase(isCloud = false).first()

        assertEquals(3, result.size)
        assertEquals("20/05/2024", result[0].issueDate)
        assertEquals("15/02/2024", result[1].issueDate)
        assertEquals("01/01/2023", result[2].issueDate)
    }

    @Test
    fun `si el repositorio devuelve una lista vacia el caso de uso debe devolver lista vacia`() = runTest {
        every { repository.getAllInvoices(any()) } returns flowOf(emptyList())
        val result = useCase(isCloud = false).first()
        assertTrue(result.isEmpty())
    }
}