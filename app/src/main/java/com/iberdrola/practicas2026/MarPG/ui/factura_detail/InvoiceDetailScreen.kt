package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.list.StatusBadge
import com.iberdrola.practicas2026.MarPG.ui.theme.*
import com.iberdrola.practicas2026.MarPG.ui.utils.toAnnotatedCurrencyFormat

@Composable
fun InvoiceDetailScreen(
    viewModel: InvoiceDetailViewModel,
    isCloudEnabled: Boolean,
    onBack: () -> Unit
) {
    val state = viewModel.state
    
    val events = InvoiceDetailEvents(
        onBack = onBack,
        onDownloadPdf = { viewModel.downloadPdf() },
        onPay = { viewModel.payInvoice(isCloudEnabled) }
    )

    InvoiceDetailContent(
        state = state,
        events = events
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailContent(
    state: InvoiceDetailState,
    events: InvoiceDetailEvents
) {
    val invoice = state.invoice ?: return

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalle de factura",
                        fontFamily = IberPangeaFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = events.onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = GreenIberdrola)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteApp,
                    scrolledContainerColor = Color.Unspecified ,
                    navigationIconContentColor = GreenIberdrola,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Unspecified
                ),
                actions = {
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
                            IconButton(onClick = events.onDownloadPdf) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudDownload,
                                    contentDescription = "Descargar PDF",
                                    tint = GreenIberdrola
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
            )
        },

        bottomBar = {
            if (invoice.status != InvoiceStatus.PAGADAS && invoice.status != InvoiceStatus.ANULADAS) {
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
                        colors = ButtonDefaults.buttonColors(containerColor = GreenIberdrola, contentColor = Color.White, disabledContainerColor = Color.Gray, disabledContentColor = Color.White),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("PAGAR FACTURA AHORA", fontFamily = IberPangeaFamily, fontWeight = FontWeight.ExtraBold, color = WhiteApp)
                        }
                    }
                }
            }
        },
        containerColor = BackgroundApp
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            InvoiceDetailHeader(
                amount = invoice.amount,
                date = DateMapper.formatToDisplay(invoice.issueDate)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    icon =Icons.Outlined.Receipt
                ) {
                    DetailRow(
                        label = "Tipo de contrato",
                        value = if (invoice.contractType == ContractType.LUZ) "Suministro de Luz" else "Suministro de Gas"
                    )
                    DetailRow(
                        label = "Número de factura",
                        value = invoice.id
                    )
                }

                InfoCard(
                    title = "Periodo de facturación",
                    icon = Icons.Outlined.Info
                ) {
                    val range = "${DateMapper.formatToShortDisplay(invoice.startDate)} - ${DateMapper.formatToShortDisplay(invoice.endDate)}"
                    Text(
                        text = range,
                        fontFamily = IberPangeaFamily,
                        fontSize =15.sp,
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
                            text = if (invoice.status == InvoiceStatus.PAGADAS) "Factura cobrada" else
                            {
                                if (invoice.status == InvoiceStatus.ANULADAS) "Factura en anulación" else "Pendiente de cobro"
                            },
                            fontFamily =IberPangeaFamily,
                            fontSize = 14.sp,
                            color = TextGrey
                        )
                        StatusBadge(invoice.status)
                    }
                }
            }
            Spacer(modifier =Modifier.height(32.dp))
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
        Text(
            text = message,
            color = color,
            fontSize = 14.sp,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontFamily = IberPangeaFamily
        )
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
            Text(
                text = "Importe total",
                fontFamily = IberPangeaFamily,
                fontSize = 14.sp,
                color = TextGrey
            )
            Text(
                text = amount.toAnnotatedCurrencyFormat(42.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF333333),
                fontFamily = IberPangeaFamily,
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = GreenIberdrola, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontFamily = IberPangeaFamily, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF333333))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextGrey, fontFamily = IberPangeaFamily)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), fontFamily = IberPangeaFamily)
    }
}
