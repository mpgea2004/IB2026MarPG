package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.iberdrola.practicas2026.MarPG.ui.components.filter.StatusFilterSection
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListViewModel
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun FilterScreen(
    listViewModel: InvoiceListViewModel,
    filterViewModel: FilterViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
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
        }
    ) { padding ->
        FilterContent(
            modifier = Modifier.padding(padding),
            state = filterViewModel.state,
            events = events,
            minLimit = listViewModel.minInvoiceAmount,
            maxLimit = listViewModel.maxInvoiceAmount
        )
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 0.dp)
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
    maxLimit: Float = 500f
) {
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    if (showFromPicker) {
        MyDatePickerDialog(
            maxDateStr = state.dateTo,
            onDateSelected = { 
                events.onDateFromChange(it)
                showFromPicker = false
            },
            onDismiss = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        MyDatePickerDialog(
            minDateStr = state.dateFrom,
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
            Text(
                text = stringResource(R.string.invoice_filter_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp),
                fontFamily = IberPangeaFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            DateRangeSection(
                dateFrom = state.dateFrom,
                dateTo = state.dateTo,
                onFromClick = { showFromPicker = true },
                onToClick = { showToPicker = true }
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(stringResource(R.string.invoice_filter_price), fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
            Spacer(modifier = Modifier.height(16.dp))

            PriceRangeSection(
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                minLimit = minLimit,
                maxLimit = maxLimit,
                onRangeChange = { min, max -> events.onPriceRangeChange(min, max) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            StatusFilterSection(
                statusOptions = statusOptions,
                selectedStatuses = state.selectedStatuses,
                onStatusToggle = { events.onStatusToggle(it) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            FilterActionButtons(
                onApply = { events.onApply() },
                onClear = { events.onClear() }
            )
        }
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

    val datePickerState = rememberDatePickerState(
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
                    Text("OK", color = GreenIberdrola, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar", color = GreenIberdrola, fontFamily = IberPangeaFamily)
                }
            }
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