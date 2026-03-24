package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.usecase.GetElectronicInvoiceUseCase
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetElectronicInvoiceUseCaseTest {

    private lateinit var useCase: GetElectronicInvoiceUseCase
    private val repository = mockk<ElectronicInvoiceRepository>()

    @Before
    fun setUp() {
        useCase = GetElectronicInvoiceUseCase(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `debe llamar al repositorio con el parametro isCloud correcto`() = runTest {
        every { repository.getAllElectronicInvoice(any()) } returns flowOf(emptyList())

        useCase(isCloud = false).first()

        verify { repository.getAllElectronicInvoice(false) }
    }

    @Test
    fun `debe emitir la lista de contratos que devuelve el repositorio`() = runTest {
        val mockList = listOf(
            ElectronicInvoice("1", ContractType.LUZ, true, "user@test.com"),
            ElectronicInvoice("2", ContractType.GAS, false, "")
        )
        every { repository.getAllElectronicInvoice(true) } returns flowOf(mockList)

        val result = useCase(isCloud = true).first()

        assertEquals(2, result.size)
        assertEquals(ContractType.LUZ, result[0].type)
        assertEquals("user@test.com", result[0].email)
    }
}