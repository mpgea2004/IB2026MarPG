package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.detail.InvoiceStepper
import com.iberdrola.practicas2026.MarPG.ui.components.detail.ShimmerInvoiceDetail
import com.iberdrola.practicas2026.MarPG.ui.components.list.StatusBadge
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.BackgroundApp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.toAnnotatedCurrencyFormat
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberTooltipState

@Composable
fun InvoiceDetailScreen(
    viewModel: InvoiceDetailViewModel,
    isCloudEnabled: Boolean,
    onBack: () -> Unit
) {
    val state = viewModel.state
    val haptic = LocalHapticFeedback.current

    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val handleBack = {
        if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }
    BackHandler(enabled = true) { handleBack() }

    val events = InvoiceDetailEvents(
        onBack = handleBack,
        onDownloadPdf = { viewModel.downloadPdf() },
        onPay = { viewModel.payInvoice(isCloudEnabled) }
    )

    InvoiceDetailContent(
        state = state,
        events = events
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InvoiceDetailContent(
    state: InvoiceDetailState,
    events: InvoiceDetailEvents
) {
    val invoice = state.invoice
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val downloadTooltipState = rememberTooltipState(isPersistent = false)
    val copyTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            FilterTopBar(onBack = events.onBack)
        },

        bottomBar = {
            if(invoice!= null) {
                if (invoice.status != InvoiceStatus.PAGADAS && invoice.status != InvoiceStatus.ANULADAS && invoice.status != InvoiceStatus.CUOTA_FIJA ) {
                    Surface(
                        color = WhiteApp,
                        shadowElevation = 8.dp
                    ) {
                        Button(
                            onClick = events.onPay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenIberdrola,
                                contentColor = Color.White,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.White
                            ),
                            enabled = !state.isLoading
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "PAGAR FACTURA AHORA",
                                    fontFamily = IberPangeaFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WhiteApp
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = BackgroundApp
    ) { padding ->
        if (invoice == null) {
            Box(Modifier
                .fillMaxSize()
                .padding(padding)) {
                ShimmerInvoiceDetail(brush = shimmerBrush())
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhiteApp)
                            .padding(horizontal = 20.dp)
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Detalle de factura",
                            fontFamily = IberPangeaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                        
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isDownloadingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = GreenIberdrola,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        Surface(
                                            color = Color.DarkGray,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Descargar factura",
                                                color = Color.White,
                                                modifier = Modifier.padding(8.dp),
                                                fontSize = 12.sp
                                            )
                                        }
                                    },
                                    state = downloadTooltipState
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .combinedClickable(
                                                onClick = { events.onDownloadPdf() },
                                                onLongClick = {
                                                    scope.launch { downloadTooltipState.show() }
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CloudDownload,
                                            contentDescription = "Descargar PDF",
                                            tint = GreenIberdrola
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    InvoiceDetailHeader(
                        amount = invoice.amount,
                        date = DateMapper.formatToDisplay(invoice.issueDate)
                    )
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = WhiteApp
                    ) {
                        InvoiceStepper(
                            status = invoice.status,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (invoice.status == InvoiceStatus.CUOTA_FIJA) {
                            FixedQuotaBanner()
                        }

                        if (state.paymentSuccess) {
                            StatusMessage(message = "¡Factura pagada correctamente!", color = GreenIberdrola)
                        }

                        if (state.paymentError) {
                            StatusMessage(message = "No se ha podido realizar el pago. Inténtalo de nuevo.", color = Color.Red)
                        }

                        if (state.pdfDownloaded) {
                            StatusMessage(message = "Factura descargada correctamente", color = GreenIberdrola)
                        }

                        InfoCard(
                            title = "Datos del suministro",
                            icon = Icons.Outlined.Receipt
                        ) {
                            DetailRow(
                                label = "Tipo de contrato",
                                value = if (invoice.contractType == ContractType.LUZ) "Suministro de Luz" else "Suministro de Gas"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Número de factura", fontSize = 14.sp, color = TextGrey, fontFamily = IberPangeaFamily)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(invoice.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), fontFamily = IberPangeaFamily)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    
                                    TooltipBox(
                                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                        tooltip = {
                                            Surface(
                                                color = Color.DarkGray,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Copiar número de factura",
                                                    color = Color.White,
                                                    modifier = Modifier.padding(8.dp),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        },
                                        state = copyTooltipState
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .combinedClickable(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        val clip = ClipData.newPlainText("ID Factura", invoice.id)
                                                        clipboard.setPrimaryClip(clip)
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        Toast.makeText(context, "ID copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                                    },
                                                    onLongClick = {
                                                        scope.launch { copyTooltipState.show() }
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ContentCopy,
                                                contentDescription = "Copiar ID",
                                                tint = GreenIberdrola,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        InfoCard(
                            title = "Periodo de facturación",
                            icon = Icons.Outlined.Info
                        ) {
                            val range = "${DateMapper.formatToShortDisplay(invoice.startDate)} - ${DateMapper.formatToShortDisplay(invoice.endDate)}"
                            Text(
                                text = range,
                                fontFamily = IberPangeaFamily,
                                fontSize = 15.sp,
                                color = Color(0xFF333333),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        InfoCard(
                            title = "Estado del pago",
                            icon = Icons.Outlined.Info
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (invoice.status == InvoiceStatus.PAGADAS || invoice.status == InvoiceStatus.CUOTA_FIJA) "Factura cobrada" else {
                                        if (invoice.status == InvoiceStatus.ANULADAS) "Factura en anulación" else "Pendiente de cobro"
                                    },
                                    fontFamily = IberPangeaFamily,
                                    fontSize = 14.sp,
                                    color = TextGrey
                                )
                                StatusBadge(invoice.status)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun FixedQuotaBanner() {
    Surface(
        color = Color(0xFFE3F2FD),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Sync, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Esta factura pertenece al Plan Cuota Fija. El importe se regularizará según tu consumo real.",
                color = Color(0xFF1976D2),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
private fun StatusMessage(message: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Info, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = color,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
private fun InvoiceDetailHeader(amount: Double, date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhiteApp)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Importe total", fontFamily = IberPangeaFamily, fontSize = 14.sp, color = TextGrey)
            Text(
                text = amount.toAnnotatedCurrencyFormat(42.sp),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = IberPangeaFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = BackgroundApp, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = "Emitida el $date",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = GreenIberdrola, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontFamily = IberPangeaFamily, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF333333))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextGrey, fontFamily = IberPangeaFamily)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), fontFamily = IberPangeaFamily)
    }
}
