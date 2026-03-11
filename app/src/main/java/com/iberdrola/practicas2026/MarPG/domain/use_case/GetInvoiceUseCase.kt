package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    //este método me permite llamar llamar a la clase como si fuera una función, y en este metodo llamo a la funcion getAllInvoices del repositorio y devuelvo la facturas ordenadas por fecha
    operator fun invoke() : Flow<List<Invoice>> {
        return repository.getAllInvoices().map{ invoices->
            invoices.sortedByDescending { DateMapper.toLocalDate(it.issueDate) }
        }
    }
}