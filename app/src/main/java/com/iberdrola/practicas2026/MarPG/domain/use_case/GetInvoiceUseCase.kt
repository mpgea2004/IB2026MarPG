package com.iberdrola.practicas2026.MarPG.domain.use_case

import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obtener y filtrar el listado de facturas
 * Aplica una ordenación descendente por fecha de emisión
 */
class GetInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    /**
     * Ejecuta la obtención de facturas y las ordena cronológicamente
     * @param isCloud Determina la fuente de datos (Remota/Local)
     * @return [Flow] con la lista de facturas ordenada de más reciente a más antigua
     */
    operator fun invoke(isCloud: Boolean) : Flow<List<Invoice>> {
        return repository.getAllInvoices(isCloud).map{ invoices->
            //Ordenación descendente usando el DateMapper para comparar fechas reales
            invoices.sortedByDescending { DateMapper.toLocalDate(it.issueDate) }
        }
    }
}