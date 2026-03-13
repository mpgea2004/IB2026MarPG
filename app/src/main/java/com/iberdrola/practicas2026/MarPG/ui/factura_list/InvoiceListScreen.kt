package com.iberdrola.practicas2026.MarPG.ui.factura_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PropaneTank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.ErrorBanner
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceNotAvailableDialog
import com.iberdrola.practicas2026.MarPG.ui.components.ShimmerInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightRedIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/** Pantalla principal del listado de facturas con filtrado por tipo y estados de carga */
@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Invoice) -> Unit
) {
    val currentState = viewModel.state
    val selectedTab = viewModel.selectedTab
    val errorMessage = viewModel.errorMessage //recupero el error del VM

    // Necesitamos esto para manejar el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    //cada vez que errorMessage cambie y no sea nulo, lanzamos el aviso
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            //y lo limpio para que no vuelva a saltar
            viewModel.clearErrorMessage()
        }
    }

    //estado para controlar el diálogo
    var showNotAvailableDialog by remember { mutableStateOf(false) }

    if (showNotAvailableDialog) {
        InvoiceNotAvailableDialog(onDismiss = { showNotAvailableDialog = false })
    }

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            //Aqui pongo todo lo que es fijo
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp).clickable {
                            viewModel.registerBackNavigation()
                            onBack() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = GreenIberdrola)
                        Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    //Si hay error y estoy en modo éxito (offline), avisamos arriba
                    if (errorMessage != null && currentState is InvoiceListState.SUCCESS) {
                        ErrorBanner(message = errorMessage)
                    }
                    Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(R.string.invoice_list_subtitle),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    //Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        InvoiceTabItem(
                            text = stringResource(R.string.invoice_list_tab_light),
                            isSelected = selectedTab == 0,
                            onClick = { viewModel.selectTab(0) }
                        )
                        InvoiceTabItem(
                            text = stringResource(R.string.invoice_list_tab_gas),
                            isSelected = selectedTab == 1,
                            onClick = { viewModel.selectTab(1) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentState) {
                InvoiceListState.LOADING -> {
                    ShimmerInvoiceList(brush = shimmerBrush())
                }
                InvoiceListState.NODATA -> {
                    InvoiceEmptyState(message = errorMessage)
                }
                is InvoiceListState.SUCCESS -> {
                    //Esto ya es el contenido
                    InvoiceListContent(
                        groupedInvoices = currentState.groupedInvoices,
                        events = InvoiceListEvents(
                            onDetail = { showNotAvailableDialog = true },
                            onFilter = { /* Abrir filtro */ }
                        )
                    )
                }
            }
        }
    }
}

/** Contenido scrolleable: última factura, cabecera fija e histórico */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InvoiceListContent(
    groupedInvoices: Map<String, List<Invoice>>,
    events: InvoiceListEvents
) {
    val lastInvoice = groupedInvoices.values.flatten().firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (groupedInvoices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.invoice_list_tap_no_invoices), color = TextGrey)
                }
            }
        } else {
            // 1. ÚLTIMA FACTURA
            lastInvoice?.let {
                item { LastInvoiceItem(it) }
            }

            // 2. TÍTULO HISTÓRICO, etse se queda anclado arriba cuando hago scroll
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.invoice_list_historic_title), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    FilterButton(onClick = { events.onFilter() })
                }
            }

            // 3.FACTURAS AGRUPADAS
            groupedInvoices.forEach { (year, invoicesOfYear) ->
                item {
                    Text(
                        text = year,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                items(invoicesOfYear) { invoice ->
                    InvoiceHistoricalItem(invoice = invoice, onClick = { events.onDetail(invoice) })
                }
            }
        }
    }
}

/** Tarjeta destacada para la factura más reciente */
@Composable
fun LastInvoiceItem(invoice: Invoice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteApp),
        border = BorderStroke(1.5.dp, GreenIberdrola)
    ) {
        Column(modifier = Modifier
            .padding(20.dp)
            .background(Color.White)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(stringResource(R.string.invoice_list_last_invoice_title), fontWeight = FontWeight.Bold)
                    Text(
                        text = stringResource(
                            id = R.string.invoice_list_invoice_type_label,
                            invoice.contractType.name.lowercase()
                        ),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    painter = if (invoice.contractType == ContractType.LUZ)
                        rememberVectorPainter(Icons.Outlined.Lightbulb) //Convierte el Vector a Painter
                    else
                        painterResource(R.drawable.ic_invoice_gas),
                    contentDescription = null,
                    tint = GreenIberdrola,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = invoice.amount.toCurrencyFormat(), fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(
                    id = R.string.invoice_list_last_invoice_date_range,
                    invoice.startDate,
                    invoice.endDate
                ),
                color = TextGrey,
                fontSize = 12.sp
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            StatusBadge(invoice.status)
        }
    }
}

/** Fila individual del histórico de facturas */
@Composable
fun InvoiceHistoricalItem(invoice: Invoice, onClick: () -> Unit) {
    val dateDisplay = DateMapper.formatToDisplay(invoice.issueDate)
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Bloque Izquierdo(Fecha, Tipo, Badge)
            Column(modifier = Modifier.weight(1f)) {
                Text(dateDisplay, fontWeight = FontWeight.Bold)
                Text(text = stringResource(
                    id = R.string.invoice_list_invoice_type_label,
                    invoice.contractType.name.lowercase()
                ), color = Color.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(invoice.status)
            }

            //Bloque Derecho(Precio yFlecha)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = invoice.amount.toCurrencyFormat(),
                    color = TextGrey,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(32.dp)

                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
}

/** Etiqueta de estado: Pagada (verde) o Pendiente (rojo) */
@Composable
fun StatusBadge(status: InvoiceStatus) {
    val isPaid = status == InvoiceStatus.PAID
    Surface(
        color = if (isPaid) LightGreenIberdrola else LightRedIberdrola,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status.description,
            color = if (isPaid) GreenIberdrola else Color(0xFFD32F2F),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Pestaña de filtrado por energía */

@Composable
fun InvoiceTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.Black else TextGrey,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
        )
        //Barra verde
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(if (isSelected) GreenIberdrola else Color.Transparent)
        )
    }
}
/** Botón de acceso a filtros */
@Composable
fun FilterButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, GreenIberdrola),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Icon(painter = painterResource(R.drawable.ic_invoice_filter), null, tint = GreenIberdrola, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.invoice_list_filter_button), color = GreenIberdrola, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
/** Formatea Double a moneda (ej: 10,50 €) */
fun Double.toCurrencyFormat(): String {
    //Formatea el Double con 2 decimales y cambia el punto por coma
    return String.format("%.2f", this).replace(".", ",") + " €"
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun InvoiceListLoadingPreview() {
    //Simulamos la estructura de la Screen pero forzando el estado LOADING
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = GreenIberdrola
                    )
                    Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.invoice_list_subtitle),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            //Fuerzo el shimmer(esqueleto)
            val brush = shimmerBrush()
            ShimmerInvoiceList(brush = brush)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun InvoiceListScreenPreview() {
    //Datos de prueba
    val mockFacturas = listOf(
        Invoice("A01", ContractType.LUZ, 20.00, "01/02/2024", "04/03/2024", "04/03/2024", InvoiceStatus.PAID),
        Invoice("A02", ContractType.GAS, 20.10, "01/01/2024", "31/01/2024", "06/02/2024", InvoiceStatus.PENDING),
        Invoice("A03", ContractType.LUZ, 150.43, "01/10/2023", "31/10/2023", "06/11/2023", InvoiceStatus.PAID)
    )

    //Agrupo usando el DateMapper
    val groupedMock = mockFacturas.groupBy { invoice ->
        DateMapper.toLocalDate(invoice.issueDate).year.toString()
    }

    MaterialTheme {
        //Uso una versión simplificada de la Screen para la Preview
        //ya que no puedo instanciar un ViewModel real con Hilt en una Preview fácilmente

        Scaffold(
            topBar = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color(0xFF008244))
                        Text(stringResource(R.string.invoice_list_back), color =GreenIberdrola, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.invoice_list_subtitle), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceListContent(
                    groupedInvoices = groupedMock,
                    events = InvoiceListEvents(),
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_8")
@Composable
fun InvoiceListNoDataPreview() {
    IB2026MarPGTheme {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color(0xFF008244))
                        Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.invoice_list_subtitle), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceEmptyState()
            }
        }
    }
}
