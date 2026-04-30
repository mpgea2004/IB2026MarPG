package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.ui.components.filter.DateRangeSection
import com.iberdrola.practicas2026.MarPG.ui.components.filter.FilterActionButtons
import com.iberdrola.practicas2026.MarPG.ui.components.filter.PriceRangeSection
import com.iberdrola.practicas2026.MarPG.ui.components.filter.ShimmerFilter
import com.iberdrola.practicas2026.MarPG.ui.components.filter.StatusFilterSection
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListViewModel
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun FilterScreen(
    listViewModel: InvoiceListViewModel,
    filterViewModel: FilterViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state = filterViewModel.state

    LaunchedEffect(Unit) {
        filterViewModel.setInitialState(listViewModel.currentFilterState)
    }

    val events = FilterEvents(
        onDateFromChange = { filterViewModel.onDateFromChange(it) },
        onDateToChange = { filterViewModel.onDateToChange(it) },
        onPriceRangeChange = { min, max -> filterViewModel.onPriceRangeChange(min, max) },
        onStatusToggle = { filterViewModel.onStatusToggle(it) },
        onClear = {
            filterViewModel.clearFilters(
                minLimit = listViewModel.minInvoiceAmount,
                maxLimit = listViewModel.maxInvoiceAmount
            )
            listViewModel.applyFilters(filterViewModel.state)
        },
        onApply = {
            listViewModel.applyFilters(filterViewModel.state)
            onBack()
        }
    )

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack)
        },
        bottomBar = {
            if (!state.isLoading) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    FilterActionButtons(
                        onApply = { events.onApply() },
                        onClear = { events.onClear() }
                    )
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            ShimmerFilter(brush = shimmerBrush())
        } else {
            FilterContent(
                modifier = Modifier.padding(padding),
                state = state,
                events = events,
                minLimit = listViewModel.minInvoiceAmount,
                maxLimit = listViewModel.maxInvoiceAmount,
                minDateLimit = listViewModel.minInvoiceDate,
                maxDateLimit = listViewModel.maxInvoiceDate
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterTopBar(onBack: () -> Unit) {
    Surface(
        color = WhiteApp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = GreenIberdrola,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(R.string.invoice_list_back),
                    color = GreenIberdrola,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontFamily = IberPangeaFamily
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    modifier: Modifier = Modifier,
    state: FilterState,
    events: FilterEvents,
    minLimit: Float = 0f,
    maxLimit: Float = 500f,
    minDateLimit: String? = null,
    maxDateLimit: String? = null
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    if (showFromPicker) {
        MyDatePickerDialog(
            minDateStr = minDateLimit,
            maxDateStr = state.dateTo.ifEmpty { maxDateLimit },
            onDateSelected = { 
                events.onDateFromChange(it)
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        MyDatePickerDialog(
            minDateStr = state.dateFrom.ifEmpty { minDateLimit },
            maxDateStr = maxDateLimit,
            onDateSelected = { 
                events.onDateToChange(it)
                showToPicker = false
            },
            onDismiss = { showToPicker = false }
        )
    }

    val statusOptions = InvoiceStatus.getAllDescriptions()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhiteApp)
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AnimateFilterItemEntrance(index = 0) {
                Text(
                    text = stringResource(R.string.invoice_filter_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp),
                    fontFamily = IberPangeaFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimateFilterItemEntrance(index = 1) {
                DateRangeSection(
                    dateFrom = state.dateFrom,
                    dateTo = state.dateTo,
                    onFromClick = { showFromPicker = true },
                    onToClick = { showToPicker = true }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))

            AnimateFilterItemEntrance(index = 2) {
                Column {
                    Text(stringResource(R.string.invoice_filter_price), fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
                    Spacer(modifier = Modifier.height(16.dp))

                    PriceRangeSection(
                        minPrice = state.minPrice,
                        maxPrice = state.maxPrice,
                        minLimit = minLimit,
                        maxLimit = maxLimit,
                        onRangeChange = { min, max -> events.onPriceRangeChange(min, max) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimateFilterItemEntrance(index = 3) {
                StatusFilterSection(
                    statusOptions = statusOptions,
                    selectedStatuses = state.selectedStatuses,
                    onStatusToggle = { events.onStatusToggle(it) }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun AnimateFilterItemEntrance(
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
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(400))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(400)),
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    minDateStr: String? = null,
    maxDateStr: String? = null,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val minDateMillis = remember(minDateStr) {
        if (!minDateStr.isNullOrEmpty()) {
            try { dateFormatter.parse(minDateStr)?.time } catch (e: Exception) { null }
        } else null
    }

    val maxDateMillis = remember(maxDateStr) {
        if (!maxDateStr.isNullOrEmpty()) {
            try { dateFormatter.parse(maxDateStr)?.time } catch (e: Exception) { null }
        } else null
    }

    val yearRange = remember(minDateMillis, maxDateMillis) {
        val calendar = Calendar.getInstance()
        val startYear = minDateMillis?.let {
            calendar.timeInMillis = it
            calendar.get(Calendar.YEAR)
        } ?: 2000
        val endYear = maxDateMillis?.let {
            calendar.timeInMillis = it
            calendar.get(Calendar.YEAR)
        } ?: 2030
        startYear..endYear
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = minDateMillis,
        yearRange = yearRange,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val isAfterMin = minDateMillis == null || utcTimeMillis >= minDateMillis
                val isBeforeMax = maxDateMillis == null || utcTimeMillis <= maxDateMillis
                return isAfterMin && isBeforeMax
            }
        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        dateFormatter.format(Date(it))
    } ?: ""

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White,
            onSurface = Color.Black,
            onSurfaceVariant = Color.DarkGray,
            primary = GreenIberdrola,
            onPrimary = Color.White
        )
    ) {
        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedDate.isNotEmpty()) onDateSelected(selectedDate)
                    onDismiss()
                }) {
                    Text(stringResource(R.string.common_ok), color = GreenIberdrola, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.common_cancel), color = GreenIberdrola, fontFamily = IberPangeaFamily)
                }
            },
            colors = DatePickerDefaults.colors(
                    containerColor = Color.White
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    headlineContentColor = Color.Black,
                    weekdayContentColor = Color.Gray,
                    subheadContentColor = Color.Black,
                    navigationContentColor = GreenIberdrola,
                    yearContentColor = Color.Black,
                    currentYearContentColor = GreenIberdrola,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = GreenIberdrola,
                    dayContentColor = Color.Black,
                    disabledDayContentColor = Color.LightGray,
                    selectedDayContainerColor = GreenIberdrola,
                    selectedDayContentColor = Color.White,
                    todayContentColor = GreenIberdrola,
                    todayDateBorderColor = GreenIberdrola
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterScreenFilledPreview() {
    IB2026MarPGTheme {
        FilterContent(
            state = FilterState(
                dateFrom = "01/01/2026",
                dateTo = "31/01/2026",
                minPrice = 20f,
                maxPrice = 150f,
                selectedStatuses = setOf("Pagadas")
            ),
            events = FilterEvents(),
        )
    }
}