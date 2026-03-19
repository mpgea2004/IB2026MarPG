package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import javax.inject.Inject

class UpdateElectronicInvoiceUseCase @Inject constructor(
    private val repository: ElectronicInvoiceRepository
) {
    suspend operator fun invoke(electronicInvoice: ElectronicInvoice) {
        repository.updateElectronicInvoice(electronicInvoice)
    }
}