package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.MarPG.data.local.preferences.UserPreferencesRepository
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.utils.InvoicePdfGenerator.generateAndSaveInvoicePdf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val repository: InvoiceRepository,
    private val userPrefs: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(InvoiceDetailState())
        private set

    init {
        observeAmountVisibility()
    }

    private fun observeAmountVisibility() {
        viewModelScope.launch {
            userPrefs.amountVisibleFlow.collect { visible ->
                state = state.copy(isAmountVisible = visible)
            }
        }
    }

    fun loadInvoice(id: String) {
        if (state.isLoading || state.invoice?.id == id) return

        viewModelScope.launch {
            state = state.copy(
                invoice = null,
                isLoading = true,
                paymentSuccess = false,
                paymentError = false,
                pdfDownloaded = false
            )

            repository.getInvoiceById(id).collect { foundInvoice ->
                if (foundInvoice != null) {
                    val issueDate = DateMapper.toLocalDate(foundInvoice.issueDate)
                    val monthsBetween = ChronoUnit.MONTHS.between(issueDate, LocalDate.now())
                    
                    val isOverdue = foundInvoice.status == InvoiceStatus.PENDIENTES_PAGO && monthsBetween > 6

                    state = state.copy(
                        invoice = foundInvoice, 
                        isLoading = false,
                        isOverdue = isOverdue
                    )
                }
            }
        }
    }

    fun setInvoice(invoice: Invoice) {
        state = state.copy(invoice = invoice, isLoading = false)
    }

    fun downloadPdf(context: Context) {
        val invoice = state.invoice ?: return
        viewModelScope.launch {
            state = state.copy(isDownloadingPdf = true)
            val uri = generateAndSaveInvoicePdf(context, invoice)
            delay(1500)
            
            state = state.copy(
                isDownloadingPdf = false, 
                pdfDownloaded = uri != null,
                pdfUri = uri,
                showPdfViewer = uri != null
            )

            delay(5000)
            state = state.copy(pdfDownloaded = false)
        }
    }
    fun dismissPdfViewer() {
        state = state.copy(showPdfViewer = false)
    }

    fun onPayClick() {
        val invoice = state.invoice ?: return
        
        if (state.isOverdue) {
            state = state.copy(showOverdueDialog = true)
        } else {
            state = state.copy(showPayPasswordDialog = true, payPasswordInput = "", payPasswordError = false)
        }
    }

    fun onPasswordChange(input: String) {
        state = state.copy(payPasswordInput = input, payPasswordError = false)
    }

    fun dismissPasswordDialog() {
        state = state.copy(showPayPasswordDialog = false)
    }

    fun dismissOverdueDialog() {
        state = state.copy(showOverdueDialog = false)
    }

    fun confirmPayment(isCloud: Boolean) {
        viewModelScope.launch {
            val userProfile = userPrefs.userProfileFlow.first()
            if (state.payPasswordInput == userProfile.password) {
                state = state.copy(showPayPasswordDialog = false)
                executePayment(isCloud)
            } else {
                state = state.copy(payPasswordError = true)
            }
        }
    }

    private fun executePayment(isCloud: Boolean) {
        val invoice = state.invoice ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true, paymentSuccess = false, paymentError = false)
            delay(2000)

            val isSuccess = Random.nextFloat() < 0.8f

            if (isSuccess) {
                try {
                    repository.payInvoice(invoice.id, isCloud)
                    state = state.copy(
                        invoice = invoice.copy(status = InvoiceStatus.PAGADAS),
                        isLoading = false,
                        paymentSuccess = true,
                        isOverdue = false
                    )
                    delay(5000)
                    state = state.copy(paymentSuccess = false)
                } catch (e: Exception) {
                    state = state.copy(isLoading = false, paymentError = true)
                }
            } else {
                state = state.copy(isLoading = false, paymentError = true)
                delay(5000)
                state = state.copy(paymentError = false)
            }
        }
    }

    fun toggleAmountVisibility() {
        viewModelScope.launch {
            userPrefs.updateAmountVisibility(!state.isAmountVisible)
        }
    }
}
