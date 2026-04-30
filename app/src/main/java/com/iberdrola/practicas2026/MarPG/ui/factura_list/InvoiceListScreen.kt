package com.iberdrola.practicas2026.MarPG.ui.factura_list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.Invoice
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper.formatToShortDisplay
import com.iberdrola.practicas2026.MarPG.ui.components.ErrorBanner
import com.iberdrola.practicas2026.MarPG.ui.components.FilterEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.ShimmerInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.list.FilterButton
import com.iberdrola.practicas2026.MarPG.ui.components.list.InvoiceTabItem
import com.iberdrola.practicas2026.MarPG.ui.components.list.StatusBadge
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterState
import com.iberdrola.practicas2026.MarPG.ui.theme.BorderLight
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.getHiddenAmountAnnotatedString
import com.iberdrola.practicas2026.MarPG.ui.utils.toAnnotatedCurrencyFormat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onBack: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToInvoiceDetail: (Invoice) -> Unit,
    onNavigateToConsumption: () -> Unit
) {

    var isNavigating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
    }

    val handleBack = {
        if (!isNavigating) {
            isNavigating = true
            viewModel.registerBackNavigation()
            onBack()
        }
    }

    val handleNavigateToConsumption = {
        if (!isNavigating) {
            isNavigating = true
            onNavigateToConsumption()
        }
    }

    BackHandler(enabled = true) { 
        viewModel.registerBackNavigation()
        handleBack() 
    }

    val currentState = viewModel.state
    val selectedTab = viewModel.selectedTab
    val errorMessage = viewModel.errorMessage
    val userAddress = viewModel.userAddress
    val isAmountVisible = viewModel.isAmountVisible
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = context.getString(it),
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage()
        }
    }

    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectTab(pagerState.currentPage)
    }

    LaunchedEffect(viewModel.selectedTab) {
        if (pagerState.currentPage != viewModel.selectedTab) {
            pagerState.animateScrollToPage(viewModel.selectedTab)
        }
    }

    if (viewModel.showSingleInvoiceDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeSingleInvoiceDialog() },
            icon = { Icon(Icons.Outlined.Info, null, tint = GreenIberdrola) },
            title = {
                Text(
                    text = stringResource(R.string.invoice_list_info_title),
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.invoice_list_single_invoice_msg),
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.closeSingleInvoiceDialog() }) {
                    Text(
                        text = stringResource(R.string.invoice_not_available_button),
                        color = GreenIberdrola,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = WhiteApp,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            InvoiceListHeader(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) },
                address = userAddress,
                onBack = handleBack,
                errorMessage = if (errorMessage != null) stringResource(errorMessage) else null,
                showErrorBanner = currentState is InvoiceListState.SUCCESS && errorMessage != null,
                onNavigateToConsumption = handleNavigateToConsumption,
                isLoading = currentState is InvoiceListState.LOADING,
                isAmountVisible = isAmountVisible,
                onToggleAmountVisibility = { viewModel.toggleAmountVisibility() }
            )
        }

    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshInvoices() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
            val listState = rememberLazyListState()
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = currentState !is InvoiceListState.LOADING
            ) { page ->
                when (currentState) {
                    InvoiceListState.LOADING -> {
                        ShimmerInvoiceList(brush = shimmerBrush())
                    }

                    InvoiceListState.NODATA -> {
                        InvoiceEmptyState(
                            message = if (errorMessage != null) stringResource(errorMessage) else null,
                            onRefresh = { viewModel.refreshInvoices() }
                        )
                    }

                    is InvoiceListState.SUCCESS -> {
                        InvoiceListContent(
                            groupedInvoices = currentState.groupedInvoices,
                            lastInvoice = currentState.lastInvoice,
                            currentSort = viewModel.currentSortOption,
                            filterState = viewModel.currentFilterState,
                            searchQuery = viewModel.searchQuery,
                            minLimit = viewModel.minInvoiceAmount,
                            maxLimit = viewModel.maxInvoiceAmount,
                            hasFilters = viewModel.hasActiveFilters(),
                            isAmountVisible = isAmountVisible,
                            listState = listState,
                            shouldScrollToHistoric = viewModel.shouldScrollToHistoric,
                            shouldScrollToTop = viewModel.shouldScrollToTop,
                            onScrollHandled = { viewModel.onScrollHandled() },
                            onScrollToTopHandled = { viewModel.onScrollToTopHandled() },
                            events = InvoiceListEvents(
                                onFilter = {
                                    if (viewModel.getCategoryInvoicesCount() <= 1) {
                                        viewModel.openSingleInvoiceDialog()
                                    } else if (!isNavigating) {
                                        isNavigating = true
                                        onNavigateToFilters()
                                    }
                                },
                                onDetail = { invoice ->
                                    if (!isNavigating) {
                                        isNavigating = true
                                        viewModel.selectInvoice(invoice)
                                        onNavigateToInvoiceDetail(invoice)
                                    }
                                },
                                onClearFilters = { viewModel.clearFilters() },
                                onSort = { viewModel.setSortOption(it) },
                                onRemoveStatus = { viewModel.removeStatusFilter(it) },
                                onRemoveDate = { viewModel.removeDateFilter() },
                                onRemovePrice = { viewModel.removePriceFilter() },
                                onRemoveSearch = { viewModel.onSearchQueryChange("") },
                                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                                onNavigateToConsumption = handleNavigateToConsumption,
                                onToggleAmountVisibility = { viewModel.toggleAmountVisibility() }
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InvoiceListHeader(
    selectedTab: Int,
    errorMessage: String?,
    address: String,
    showErrorBanner: Boolean,
    onTabSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onNavigateToConsumption: () -> Unit,
    isLoading: Boolean,
    isAmountVisible: Boolean,
    onToggleAmountVisibility: () -> Unit
){
    val tooltipState = rememberTooltipState(isPersistent = false)
    val visibilityTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Surface(
        color = WhiteApp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onBack() }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = GreenIberdrola,
                        modifier = Modifier.size(32.dp))
                    Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TooltipBox(
                        positionProvider = rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            Surface(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isAmountVisible) stringResource(R.string.invoice_list_hide_amounts) else stringResource(R.string.invoice_list_show_amounts),
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 12.sp
                                )
                            }
                        },
                        state = visibilityTooltipState
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GreenIberdrola.copy(alpha = 0.1f))
                                .combinedClickable(
                                    onClick = onToggleAmountVisibility,
                                    onLongClick = {
                                        haptic.performHapticFeedback(LongPress)
                                        scope.launch { visibilityTooltipState.show() }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = stringResource(R.string.invoice_list_change_visibility),
                                tint = GreenIberdrola,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    TooltipBox(
                        positionProvider = rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            Surface(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.invoice_list_view_consumption),
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 12.sp
                                )
                            }
                        },
                        state = tooltipState
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isLoading) Color.LightGray.copy(alpha = 0.1f) else GreenIberdrola.copy(alpha = 0.1f)
                                )
                                .combinedClickable(
                                    enabled = !isLoading,
                                    onClick = onNavigateToConsumption,
                                    onLongClick = {
                                        haptic.performHapticFeedback(LongPress)
                                        scope.launch { tooltipState.show() }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BarChart,
                                contentDescription = stringResource(R.string.invoice_list_view_consumption),
                                tint = if (isLoading) Color.Gray else GreenIberdrola,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (showErrorBanner && errorMessage != null) {
                ErrorBanner(message = errorMessage)
            }
            
            Text(stringResource(R.string.invoice_list_title), fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily, color = Color.Black)
            
            Text(
                text = address.ifEmpty { stringResource(R.string.profile_empty_address) },
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
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
                InvoiceTabItem(
                    text = stringResource(R.string.invoice_list_tab_gas),
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1)}
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListContent(
    groupedInvoices: Map<String, List<Invoice>>,
    lastInvoice: Invoice?,
    currentSort: SortOption,
    filterState: FilterState,
    searchQuery: String,
    minLimit: Float,
    maxLimit: Float,
    hasFilters: Boolean,
    isAmountVisible: Boolean,
    listState: LazyListState,
    shouldScrollToHistoric: Boolean,
    shouldScrollToTop: Boolean,
    onScrollHandled: () -> Unit,
    onScrollToTopHandled: () -> Unit,
    events: InvoiceListEvents
) {
    val searchTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()

    LaunchedEffect(shouldScrollToHistoric) {
        if (shouldScrollToHistoric) {
            delay(300) 
            listState.animateScrollToItem(index = 1)
            onScrollHandled()
        }
    }

    LaunchedEffect(shouldScrollToTop) {
        if (shouldScrollToTop) {
            delay(300)
            listState.animateScrollToItem(index = 0)
            onScrollToTopHandled()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        lastInvoice?.let {
            item {
                AnimateItemEntrance(index = 0) {
                    LastInvoiceItem(it, isAmountVisible = isAmountVisible, onClick = { events.onDetail(it) })
                }
            }
        }

        stickyHeader {
            Column(modifier = Modifier.background(WhiteApp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = events.onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        placeholder = { Text(stringResource(R.string.invoice_list_search_placeholder), fontSize = 14.sp) },
                        leadingIcon = {
                            TooltipBox(
                                positionProvider = rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    Surface(
                                        color = Color.DarkGray,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.invoice_list_search_desc),
                                            color = Color.White,
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                state = searchTooltipState
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = stringResource(R.string.invoice_list_search_desc),
                                    tint = GreenIberdrola,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .combinedClickable(
                                            onClick = {  },
                                            onLongClick = {
                                                scope.launch { searchTooltipState.show() }
                                            }
                                        )
                                )
                            }
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { events.onSearchQueryChange("") }) {
                                    Icon(Icons.Outlined.Close, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.1f),
                            focusedIndicatorColor = GreenIberdrola,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = GreenIberdrola,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(26.dp),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.invoice_list_historic_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = IberPangeaFamily,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (hasFilters) {
                            OutlinedButton(
                                onClick = events.onClearFilters,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.5.dp, GreenIberdrola),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenIberdrola, containerColor = GreenIberdrola.copy(alpha = 0.1f))
                            ) {
                                Text(stringResource(R.string.invoice_list_clear_filters), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Outlined.Close, null, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        FilterButton(onClick = { events.onFilter() })
                    }
                }
                
                ActiveFiltersRow(
                    state = filterState,
                    searchQuery = searchQuery,
                    minLimit = minLimit,
                    maxLimit = maxLimit,
                    onRemoveStatus = events.onRemoveStatus,
                    onRemoveDate = events.onRemoveDate,
                    onRemovePrice = events.onRemovePrice,
                    onRemoveSearch = events.onRemoveSearch
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp,top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        SortChip(
                            text = stringResource(R.string.invoice_list_label_date),
                            isSelected = currentSort == SortOption.DATE,
                            onClick = { events.onSort(SortOption.DATE) }
                        )
                    }
                    item {
                        SortChip(
                            text = stringResource(R.string.invoice_list_label_amount),
                            isSelected = currentSort == SortOption.PRICE,
                            onClick = { events.onSort(SortOption.PRICE) }
                        )
                    }
                    item {
                        SortChip(
                            text = stringResource(R.string.invoice_list_label_states),
                            isSelected = currentSort == SortOption.TYPE,
                            onClick = { events.onSort(SortOption.TYPE) }
                        )
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            }
        }

        if (groupedInvoices.isEmpty()) {
            item {
                FilterEmptyState()
            }
        } else {
            var globalIndex = 1
            groupedInvoices.forEach { (year, invoicesOfYear) ->
                item {
                    AnimateItemEntrance(index = globalIndex++) {
                        Text(
                            text = year,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            fontFamily = IberPangeaFamily
                        )
                    }
                }
                itemsIndexed(invoicesOfYear) { index, invoice ->
                    AnimateItemEntrance(index = globalIndex++) {
                        Column {
                            InvoiceHistoricalItem(invoice = invoice, isAmountVisible = isAmountVisible, onClick = { events.onDetail(invoice) })
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimateItemEntrance(
    index: Int,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 500, delayMillis = (index * 50).coerceAtMost(300))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 500, delayMillis = (index * 50).coerceAtMost(300)),
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
    }
}

@Composable
fun ActiveFiltersRow(
    state: FilterState,
    searchQuery: String,
    minLimit: Float,
    maxLimit: Float,
    onRemoveStatus: (String) -> Unit,
    onRemoveDate: () -> Unit,
    onRemovePrice: () -> Unit,
    onRemoveSearch: () -> Unit
) {
    val isDefaultPrice = abs(state.minPrice - minLimit) < 0.01f && abs(state.maxPrice - maxLimit) < 0.01f
    val hasDate = state.dateFrom.isNotEmpty() || state.dateTo.isNotEmpty()

    if (state.selectedStatuses.isNotEmpty() || hasDate || !isDefaultPrice || searchQuery.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=12.dp)
                .animateContentSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (searchQuery.isNotEmpty()) {
                item { ActiveFilterChip(text = stringResource(R.string.invoice_list_filter_id, searchQuery), onRemove = onRemoveSearch) }
            }
            items(state.selectedStatuses.toList()) { status ->
                ActiveFilterChip(text = status, onRemove = { onRemoveStatus(status) })
            }

            if (hasDate) {
                item { 
                    val dateText = if (state.dateFrom.isNotEmpty() && state.dateTo.isNotEmpty()) {
                        "${state.dateFrom} - ${state.dateTo}"
                    } else if (state.dateFrom.isNotEmpty()) {
                        stringResource(R.string.invoice_list_filter_date_from, state.dateFrom)
                    } else {
                        stringResource(R.string.invoice_list_filter_date_to, state.dateTo)
                    }
                    ActiveFilterChip(text = dateText, onRemove = onRemoveDate) 
                }
            }

            if (!isDefaultPrice) {
                item { 
                    ActiveFilterChip(
                        text = stringResource(R.string.filter_price_range, state.minPrice.toInt(), state.maxPrice.toInt()), 
                        onRemove = onRemovePrice
                    ) 
                }
            }
        }
    }
}

@Composable
fun ActiveFilterChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.height(28.dp),
        shape = RoundedCornerShape(14.dp),
        color = GreenIberdrola.copy(alpha = 0.1f),
        border = BorderStroke(0.5.dp, GreenIberdrola.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GreenIberdrola,
                fontFamily = IberPangeaFamily
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.invoice_list_remove_filter),
                tint = GreenIberdrola,
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .clickable { onRemove() }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SortChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    TooltipBox(
        positionProvider = rememberPlainTooltipPositionProvider(),
        tooltip = {
            Surface(
                color = Color.DarkGray,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.invoice_list_sort_by, text),
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    fontSize = 12.sp
                )
            }
        },
        state = tooltipState
    ) {
        Surface(
            modifier = Modifier
                .height(32.dp)
                .clip(RoundedCornerShape(20.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { tooltipState.show() }
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            color = if (isSelected) GreenIberdrola else Color.Transparent,
            border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = IberPangeaFamily
                )
            }
        }
    }
}

@Composable
fun LastInvoiceItem(invoice: Invoice, isAmountVisible: Boolean, onClick: () -> Unit) {

    val startDateFormatted = formatToShortDisplay(invoice.startDate)
    val endDateFormatted = formatToShortDisplay(invoice.endDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteApp),
        border = BorderStroke(1.5.dp, GreenIberdrola)
    ) {
        Column(modifier = Modifier
            .padding(20.dp)
            .background(WhiteApp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(stringResource(R.string.invoice_list_last_invoice_title), fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
                    Text(
                        text = stringResource(
                            id = R.string.invoice_list_invoice_type_label,
                            invoice.contractType.name.lowercase().replaceFirstChar { it.uppercase() }
                        ),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = IberPangeaFamily
                    )
                }
                Icon(
                    painter = if (invoice.contractType == ContractType.LUZ)
                        rememberVectorPainter(Icons.Outlined.Lightbulb)
                    else
                        painterResource(R.drawable.ic_invoice_gas),
                    contentDescription = null,
                    tint = GreenIberdrola,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            if (isAmountVisible) {
                Text(
                    text = invoice.amount.toAnnotatedCurrencyFormat(28.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontFamily = IberPangeaFamily
                )
            } else {
                Text(
                    text = getHiddenAmountAnnotatedString(28.sp),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontFamily = IberPangeaFamily
                )
            }

            Text(
                text = stringResource(R.string.invoice_list_last_invoice_date_range, startDateFormatted, endDateFormatted),
                color = TextGrey,
                fontSize = 12.sp,
                fontFamily = IberPangeaFamily
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = BorderLight
            )
            StatusBadge(invoice.status)
        }
    }
}

@Composable
fun InvoiceHistoricalItem(invoice: Invoice, isAmountVisible: Boolean, onClick: () -> Unit) {
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
                Text(dateDisplay, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily)
                Text(text = stringResource(
                    id = R.string.invoice_list_invoice_type_label,
                    invoice.contractType.name.lowercase().replaceFirstChar { it.uppercase() }),
                    color = Color.Black, fontSize = 14.sp,fontFamily = IberPangeaFamily)
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(invoice.status)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isAmountVisible) {
                    Text(
                        text = invoice.amount.toAnnotatedCurrencyFormat(14.sp, 14.sp),
                        color = TextGrey,
                        fontWeight = FontWeight.Medium,
                        fontFamily = IberPangeaFamily
                    )
                } else {
                    Text(
                        text = getHiddenAmountAnnotatedString(14.sp, 14.sp),
                        color = TextGrey,
                        fontWeight = FontWeight.Medium,
                        fontFamily = IberPangeaFamily
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextGrey,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
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
                    address = "Calle falsa 123",
                    onNavigateToConsumption = {},
                    isLoading = false,
                    isAmountVisible = true,
                    onToggleAmountVisibility = {}
                )
            }
        ){ padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceListContent(
                    groupedInvoices = groupedMock,
                    lastInvoice = mockFacturas.first(),
                    hasFilters = false,
                    currentSort = SortOption.DATE,
                    filterState = FilterState(),
                    searchQuery = "",
                    minLimit = 0f,
                    maxLimit = 500f,
                    isAmountVisible = true,
                    listState = rememberLazyListState(),
                    shouldScrollToHistoric = false,
                    shouldScrollToTop = false,
                    onScrollHandled = {},
                    onScrollToTopHandled = {},
                    events = InvoiceListEvents(
                        onFilter = {},
                        onDetail = {},
                        onClearFilters = {},
                        onSort = {},
                        onRemoveStatus = {},
                        onRemoveDate = {},
                        onRemovePrice = {},
                        onRemoveSearch = {},
                        onSearchQueryChange = {}
                    ),
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
                    address = "Sin dirección",
                    onNavigateToConsumption = {},
                    isLoading = false,
                    isAmountVisible = true,
                    onToggleAmountVisibility = {}
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                InvoiceEmptyState()
            }
        }
    }
}