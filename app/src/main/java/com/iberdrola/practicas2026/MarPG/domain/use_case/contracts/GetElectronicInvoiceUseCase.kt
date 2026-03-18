package com.iberdrola.practicas2026.MarPG.domain.usecase

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de contratos de la aplicación
 * Sigue la lógica de negocio de decidir si se sincroniza con la nube o no
 */
class GetElectronicInvoiceUseCase @Inject constructor(
    private val repository: ElectronicInvoiceRepository
) {
    /**
     * Al invocar el caso de uso, devuelve el flujo de contratos
     * @param isCloud Indica si debe intentar refrescar los datos desde la API
     */
    operator fun invoke(isCloud: Boolean = true): Flow<List<ElectronicInvoice>> {
        return repository.getAllElectronicInvoice(isCloud)
    }
}