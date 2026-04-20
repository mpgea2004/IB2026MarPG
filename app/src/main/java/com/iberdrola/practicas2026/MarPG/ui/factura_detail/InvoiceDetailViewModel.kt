package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val repository: InvoiceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(InvoiceDetailState())
        private set

    fun loadInvoice(id: String) {
        viewModelScope.launch {
            state = state.copy(
                invoice = null,
                isLoading = true,
                paymentSuccess = false,
                paymentError = false,
                pdfDownloaded = false
            )

            try {
                val invoices = repository.getAllInvoices(false).first()
                val found = invoices.find { it.id == id }

                delay(100)

                state = state.copy(invoice = found, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(isLoading = false)
            }
        }
    }

    fun setInvoice(invoice: Invoice) {
        state = state.copy(invoice = invoice, isLoading = false)
    }

    fun downloadPdf() {
        viewModelScope.launch {
            state = state.copy(isDownloadingPdf = true)
            delay(2000)
            state = state.copy(isDownloadingPdf = false, pdfDownloaded = true)
            delay(3000)
            state = state.copy(pdfDownloaded = false)
        }
    }

    fun payInvoice(isCloud: Boolean) {
        val invoice = state.invoice ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, paymentSuccess = false, paymentError = false)
            delay(2000)

            val isSuccess = Random.nextFloat() < 0.7f

            if (isSuccess) {
                try {
                    repository.payInvoice(invoice.id, isCloud)
                    state = state.copy(
                        invoice = invoice.copy(status = InvoiceStatus.PAGADAS),
                        isLoading = false,
                        paymentSuccess = true
                    )
                    delay(3000)
                    state = state.copy(paymentSuccess = false)
                } catch (e: Exception) {
                    state = state.copy(isLoading = false, paymentError = true)
                }
            } else {
                state = state.copy(isLoading = false, paymentError = true)
                delay(3000)
                state = state.copy(paymentError = false)
            }
        }
    }
}