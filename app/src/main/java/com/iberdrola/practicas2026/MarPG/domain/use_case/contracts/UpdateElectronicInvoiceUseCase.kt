package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.domain.repository.ElectronicInvoiceRepository
import javax.inject.Inject

class UpdateElectronicInvoiceUseCase @Inject constructor(
    private val repository: ElectronicInvoiceRepository
) {
    suspend operator fun invoke(electronicInvoice: ElectronicInvoice, newEmail: String) {
        //Creo una copia de la fact elect con los nuevos datos
        val updatedElectronicInvoice = electronicInvoice.copy(
            email = newEmail,
            isEnabled = true
        )

        //Se lo paso al repositorio (la interfaz de Domain)
        repository.updateElectronicInvoice(updatedElectronicInvoice)
    }
}