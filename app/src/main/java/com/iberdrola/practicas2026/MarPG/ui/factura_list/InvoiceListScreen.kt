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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.ErrorBanner
import com.iberdrola.practicas2026.MarPG.ui.components.FilterEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceNotAvailableDialog
import com.iberdrola.practicas2026.MarPG.ui.components.ShimmerInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.list.FilterButton
import com.iberdrola.practicas2026.MarPG.ui.components.list.InvoiceTabItem
import com.iberdrola.practicas2026.MarPG.ui.components.list.StatusBadge
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.toCurrencyFormat

/** Pantalla principal del listado de facturas con filtrado por tipo y estados de carga */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onBack: () -> Unit,
    onNavigateToFilters:() -> Unit
) {
    val currentState = viewModel.state
    val selectedTab = viewModel.selectedTab
    val errorMessage = viewModel.errorMessage
    val userAddress = viewModel.userAddress

    val snackbarHostState = remember { SnackbarHostState() }

    val analytics = Firebase.analytics
    val remoteConfig = Firebase.remoteConfig
    var isGasEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Lista_Facturas_Mar")
        }

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isGasEnabled = remoteConfig.getBoolean("show_gas_contracts")
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage()
        }
    }

    var showNotAvailableDialog by remember { mutableStateOf(false) }

    if (showNotAvailableDialog) {
        InvoiceNotAvailableDialog(onDismiss = { showNotAvailableDialog = false })
    }

    val pagerState = rememberPagerState(pageCount = { if (isGasEnabled) 2 else 1 })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectTab(pagerState.currentPage)
    }

    LaunchedEffect(viewModel.selectedTab) {
        if (pagerState.currentPage != viewModel.selectedTab) {
            pagerState.animateScrollToPage(viewModel.selectedTab)
        }
    }

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            InvoiceListHeader(
                selectedTab = selectedTab,
                isGasEnabled = isGasEnabled,
                onTabSelected = { viewModel.selectTab(it) },
                address = userAddress,
                onBack = {
                    viewModel.registerBackNavigation()
                    onBack()
                },
                errorMessage = errorMessage,
                showErrorBanner = currentState is InvoiceListState.SUCCESS && errorMessage != null            )
        }

    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshInvoices() },
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = currentState !is InvoiceListState.LOADING && isGasEnabled
            ) { page ->
                when (currentState) {
                    InvoiceListState.LOADING -> {
                        ShimmerInvoiceList(brush = shimmerBrush())
                    }

                    InvoiceListState.NODATA -> {
                        InvoiceEmptyState(message = errorMessage)
                    }

                    is InvoiceListState.SUCCESS -> {
                        InvoiceListContent(
                            groupedInvoices = currentState.groupedInvoices,
                            events = InvoiceListEvents(
                                onFilter = {
                                    analytics.logEvent("click_filter_invoices") { param("action", "open_filters") }
                                    onNavigateToFilters()
                                },
                                onDetail = { invoice ->
                                    analytics.logEvent("click_invoice_detail") {
                                        param("type", invoice.contractType.name)
                                        param("amount", invoice.amount.toString())
                                    }
                                    showNotAvailableDialog = true
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

/** Componente de cabecera que contiene el botón de atrás, títulos y pestañas de selección */
@Composable
fun InvoiceListHeader(
    selectedTab: Int,
    isGasEnabled: Boolean,
    errorMessage: String?,
    address: String,
    showErrorBanner: Boolean,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit
){
    Surface(
        color = WhiteApp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp).clickable { onBack() }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = GreenIberdrola,                    modifier = Modifier.size(32.dp))
                Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (showErrorBanner && errorMessage != null) {
                ErrorBanner(message = errorMessage)
            }
            Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                address.ifEmpty { stringResource(R.string.profile_empty_address) },
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                InvoiceTabItem(
                    text = stringResource(R.string.invoice_list_tab_light),
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0)}
                )
                if (isGasEnabled) {
                    InvoiceTabItem(
                        text = stringResource(R.string.invoice_list_tab_gas),
                        isSelected = selectedTab == 1,
                        onClick = { onTabSelected(1) }
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
        lastInvoice?.let {
            item { LastInvoiceItem(it) }
        }

        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.invoice_list_historic_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                FilterButton(onClick = { events.onFilter() })
            }
        }

        if (groupedInvoices.isEmpty()) {
            item {
                FilterEmptyState()
            }
        } else {
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

/** Tarjeta destacada para la factura más reciente, esta también se ve afectada por el filtrado */
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
            Column(modifier = Modifier.weight(1f)) {
                Text(dateDisplay, fontWeight = FontWeight.Bold)
                Text(text = stringResource(
                    id = R.string.invoice_list_invoice_type_label,
                    invoice.contractType.name.lowercase()
                ), color = Color.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(invoice.status)
            }

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

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun InvoiceListLoadingPreview() {
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
            val brush = shimmerBrush()
            ShimmerInvoiceList(brush = brush)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun InvoiceListScreenPreview() {
    val mockFacturas = listOf(
        Invoice("A01", ContractType.LUZ, 20.00, "01/02/2024", "04/03/2024", "04/03/2024", InvoiceStatus.PAGADAS),
        Invoice("A02", ContractType.GAS, 20.10, "01/01/2024", "31/01/2024", "06/02/2024", InvoiceStatus.PENDIENTES_PAGO),
        Invoice("A03", ContractType.LUZ, 150.43, "01/10/2023", "31/10/2023", "06/11/2023", InvoiceStatus.PAGADAS)
    )

    val groupedMock = mockFacturas.groupBy { invoice ->
        DateMapper.toLocalDate(invoice.issueDate).year.toString()
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                InvoiceListHeader(
                    selectedTab = 0,
                    errorMessage = null,
                    showErrorBanner = false,
                    onTabSelected = {},
                    onBack = {},
                    address = stringResource(R.string.invoice_list_subtitle),
                    isGasEnabled = true
                )
            }
        ){ padding ->
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
                InvoiceListHeader(
                    selectedTab = 0,
                    errorMessage = null,
                    showErrorBanner = false,
                    onTabSelected = {},
                    onBack = {},
                    address = stringResource(R.string.invoice_list_subtitle),
                    isGasEnabled = false
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceEmptyState()
            }
        }
    }
}
