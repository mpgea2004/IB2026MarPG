package com.iberdrola.practicas2026.MarPG.domain.usecase

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetElectronicInvoiceUseCase @Inject constructor(
    private val repository: ElectronicInvoiceRepository
) {

    operator fun invoke(isCloud: Boolean = true): Flow<List<ElectronicInvoice>> {
        return repository.getAllElectronicInvoice(isCloud)
    }
}