package com.iberdrola.practicas2026.MarPG.domain.use_case.invoice

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    operator fun invoke(isCloud: Boolean) : Flow<List<Invoice>> {
        return repository.getAllInvoices(isCloud).map { invoices->
            invoices.sortedByDescending { DateMapper.toLocalDate(it.issueDate) }
        }
    }
}