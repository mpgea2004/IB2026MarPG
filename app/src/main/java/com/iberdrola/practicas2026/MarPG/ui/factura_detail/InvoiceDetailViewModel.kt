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
import com.iberdrola.practicas2026.MarPG.domain.resository.AnalyticsPriority
import com.iberdrola.practicas2026.MarPG.domain.use_case.contracts.VerifyUserPasswordUseCase
import com.iberdrola.practicas2026.MarPG.domain.use_case.events.LogAnalyticsEventUseCase
import com.iberdrola.practicas2026.MarPG.domain.resository.InvoiceRepository
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.utils.InvoicePdfGenerator.generateAndSaveInvoicePdf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val repository: InvoiceRepository,
    private val userPrefs: UserPreferencesRepository,
    private val logAnalyticsUseCase: LogAnalyticsEventUseCase,
    private val verifyUserPasswordUseCase: VerifyUserPasswordUseCase,
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
            logAnalyticsUseCase("view_detalle_factura", mapOf("id_factura" to id), priority = AnalyticsPriority.HIGH)
            state = state.copy(
                invoice = null,
                isLoading = true,
                paymentSuccess = false,
                paymentError = false,
                pdfDownloaded = false,
                pdfError = false
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
        logAnalyticsUseCase("click_descargar_pdf_factura", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.MEDIUM)
        
        viewModelScope.launch {
            state = state.copy(isDownloadingPdf = true, pdfError = false, pdfDownloaded = false)
            
            try {
                val uri = generateAndSaveInvoicePdf(context, invoice)
                delay(1500)
                
                if (uri != null) {
                    logAnalyticsUseCase("exito_descarga_pdf", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.HIGH)
                    state = state.copy(
                        isDownloadingPdf = false, 
                        pdfDownloaded = true,
                        pdfUri = uri,
                        showPdfViewer = true
                    )
                } else {
                    logAnalyticsUseCase("error_descarga_pdf", mapOf("motivo" to "URI nula"), priority = AnalyticsPriority.HIGH)
                    state = state.copy(
                        isDownloadingPdf = false,
                        pdfError = true
                    )
                }
            } catch (e: Exception) {
                logAnalyticsUseCase("error_descarga_pdf", mapOf("mensaje" to (e.message ?: "error desconocido")), priority = AnalyticsPriority.HIGH)
                state = state.copy(
                    isDownloadingPdf = false,
                    pdfError = true
                )
            }

            delay(5000)
            state = state.copy(pdfDownloaded = false, pdfError = false)
        }
    }

    fun dismissPdfViewer() {
        state = state.copy(showPdfViewer = false)
    }

    fun onPayClick() {
        val invoice = state.invoice ?: return
        logAnalyticsUseCase("click_pagar_factura", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.MEDIUM)
        
        if (state.isOverdue) {
            logAnalyticsUseCase("view_aviso_factura_vencida", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.LOW)
            state = state.copy(showOverdueDialog = true)
        } else {
            state = state.copy(showPayPasswordDialog = true, payPasswordInput = "", payPasswordError = false)
        }
    }

    fun onPasswordChange(input: String) {
        state = state.copy(payPasswordInput = input, payPasswordError = false)
    }

    fun dismissPasswordDialog() {
        logAnalyticsUseCase("click_cancelar_pago", priority = AnalyticsPriority.LOW)
        state = state.copy(showPayPasswordDialog = false)
    }

    fun dismissOverdueDialog() {
        state = state.copy(showPayPasswordDialog = false, showOverdueDialog = false)
    }

    fun confirmPayment(isCloud: Boolean) {
        viewModelScope.launch {
            if (verifyUserPasswordUseCase(state.payPasswordInput)) {
                logAnalyticsUseCase("verificacion_pago_correcta", priority = AnalyticsPriority.HIGH)
                state = state.copy(showPayPasswordDialog = false)
                executePayment(isCloud)
            } else {
                logAnalyticsUseCase("error_pago_contraseña_incorrecta", priority = AnalyticsPriority.HIGH)
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
                    logAnalyticsUseCase("exito_pago_factura", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.HIGH)
                    state = state.copy(
                        invoice = invoice.copy(status = InvoiceStatus.PAGADAS),
                        isLoading = false,
                        paymentSuccess = true,
                        isOverdue = false
                    )
                    delay(5000)
                    state = state.copy(paymentSuccess = false)
                } catch (e: Exception) {
                    logAnalyticsUseCase("error_pago_factura_api", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.HIGH)
                    state = state.copy(isLoading = false, paymentError = true)
                }
            } else {
                logAnalyticsUseCase("error_pago_factura_simulado", mapOf("id_factura" to invoice.id), priority = AnalyticsPriority.HIGH)
                state = state.copy(isLoading = false, paymentError = true)
                delay(5000)
                state = state.copy(paymentError = false)
            }
        }
    }

    fun toggleAmountVisibility() {
        val nuevoEstado = if (!state.isAmountVisible) "Visible" else "Oculto"
        logAnalyticsUseCase("click_alternar_visibilidad_detalle", mapOf("estado_final" to nuevoEstado), priority = AnalyticsPriority.LOW)
        viewModelScope.launch {
            userPrefs.updateAmountVisibility(!state.isAmountVisible)
        }
    }
}
