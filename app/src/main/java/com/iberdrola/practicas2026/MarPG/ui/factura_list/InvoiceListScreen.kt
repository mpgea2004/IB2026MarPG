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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceNotAvailableDialog
import com.iberdrola.practicas2026.MarPG.ui.components.ShimmerInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme

@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onBack: () -> Unit,
    onNavigateToDetail: (Invoice) -> Unit
) {
    val currentState = viewModel.state
    val selectedTab = viewModel.selectedTab

    //estado para controlar el diálogo
    var showNotAvailableDialog by remember { mutableStateOf(false) }

    if (showNotAvailableDialog) {
        InvoiceNotAvailableDialog(onDismiss = { showNotAvailableDialog = false })
    }

    Scaffold(
        containerColor = Color.White,
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
                        modifier = Modifier.clickable { onBack() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = Color(0xFF008244))
                        Text("Atrás", color = Color(0xFF008244), fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Mis facturas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "C/ PALMA - ARTA KM 49, 5, 4ºA -PINTO - MADRID",
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
                            text = "Luz",
                            isSelected = selectedTab == 0,
                            onClick = { viewModel.selectTab(0) }
                        )
                        InvoiceTabItem(
                            text = "Gas",
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
                    InvoiceEmptyState()
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
                    Text("No tienes facturas de este tipo", color = Color.Gray)
                }
            }
        } else {
            // 1. ÚLTIMA FACTURA
            lastInvoice?.let {
                item { LastInvoiceItem(it) }
            }

            // 2. TÍTULO HISTÓRICO, etse se queda anclado arriba cuando hago scroll
            stickyHeader {
                Column(modifier = Modifier.background(Color.White)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Histórico de facturas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        FilterButton(onClick = { events.onFilter() })
                    }
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

@Composable
fun LastInvoiceItem(invoice: Invoice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier
            .padding(20.dp)
            .background(Color.White)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Última factura", fontWeight = FontWeight.Bold)
                    Text("Factura ${invoice.contractType.name.lowercase()}", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Icon(if (invoice.contractType == ContractType.LUZ) Icons.Outlined.Lightbulb else Icons.Outlined.PropaneTank,
                    contentDescription = null, tint = Color(0xFF008244))
            }
            Text(text = invoice.amount.toCurrencyFormat(), fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Text("${invoice.startDate} - ${invoice.endDate}", color = Color.Gray, fontSize = 12.sp)
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            StatusBadge(invoice.status)
        }
    }
}

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
                Text("Factura ${invoice.contractType.name.lowercase()}", color = Color.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(invoice.status)
            }

            //Bloque Derecho(Precio yFlecha)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = invoice.amount.toCurrencyFormat(),
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray,
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

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val isPaid = status == InvoiceStatus.PAID
    Surface(
        color = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFDECEA),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status.description,
            color = if (isPaid) Color(0xFF008244) else Color(0xFFD32F2F),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InvoiceEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No tienes facturas disponibles",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "En este momento no hay facturas para mostrar en esta categoría. Si crees que es un error, contacta con atención al cliente.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

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
            color = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp)
        )
        //Barra verde
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(if (isSelected) Color(0xFF008244) else Color.Transparent)
        )
    }
}

@Composable
fun FilterButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF008244)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Icon(Icons.Default.Tune, null, tint = Color(0xFF008244), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Filtrar", color = Color(0xFF008244), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

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
                        tint = Color(0xFF008244)
                    )
                    Text("Atrás", color = Color(0xFF008244), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Mis facturas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(
                    "C/ PALMA - ARTA KM 49, 5, 4ºA -PINTO - MADRID",
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
                        Text("Atrás", color = Color(0xFF008244), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Mis facturas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("C/ PALMA - ARTA KM 49, 5, 4ºA -PINTO - MADRID", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        Text("Atrás", color = Color(0xFF008244), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Mis facturas", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("C/ PALMA - ARTA KM 49, 5, 4ºA -PINTO - MADRID", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceEmptyState()
            }
        }
    }
}
