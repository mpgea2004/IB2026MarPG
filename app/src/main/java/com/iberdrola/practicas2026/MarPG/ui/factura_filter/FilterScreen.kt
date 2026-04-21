package com.iberdrola.practicas2026.MarPG.ui.factura_filter

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Locale.getDefault

/**
 * Pantalla principal de filtrado de facturas
 * Se encarga de coordinar la sincronización entre el ViewModel de la lista y el de filtros,
 * además de gestionar la navegación de retorno
 */
@Composable
fun FilterScreen(
    listViewModel: InvoiceListViewModel, //Viene de la pantalla anterior
    filterViewModel: FilterViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    /**
     * Sincroniza lo qu etenga en el viewmodel con el estado del filtro
     */
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
            //Paso el paquete de datos final al ViewModel de la lista
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
/**
 * Barra superior personalizada para la pantalla de filtros
 * Incluye el botón de volver atrás
 */
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

/**
 * Contenedor del formulario de filtrado
 * Organiza las secciones de Fecha, Importe y Estado, además de gestionar
 * la visibilidad de los diálogos de selección de fecha
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    modifier: Modifier = Modifier,
    state: FilterState,
    events: FilterEvents,
    minLimit: Float = 0f,
    maxLimit: Float = 500f
) {
    val context = LocalContext.current

    //Estados del datepicker
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    //Logica para enseñar el datepicker
    if (showFromPicker) {
        MyDatePickerDialog(
            onDateSelected = { events.onDateFromChange(it) },
            onDismiss = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        MyDatePickerDialog(
            minDateStr = state.dateFrom,
            onDateSelected = { events.onDateToChange(it) },
            onDismiss = { showToPicker = false }
        )
    }

    val statusOptions = InvoiceStatus.getAllDescriptions()

    // COLUMNA PRINCIPAL: Ocupa toda la pantalla
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhiteApp)
    ){
        //PARTE SUPERIOR: Formulario con Scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // Título principal
            Text(
                text = stringResource(R.string.invoice_filter_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp),
                fontFamily = IberPangeaFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección fecha
            DateRangeSection(
                dateFrom = state.dateFrom,
                dateTo = state.dateTo,
                onFromClick = { showFromPicker = true },
                onToClick = {
                    //Solo dejo que escoja el hasta si el desde ya está
                    if (state.dateFrom.isNotEmpty()) {
                        showToPicker = true
                    } else {
                        // Si el usuario pulsa y está vacío, aviso
                        Toast.makeText(
                            context,
                            "Selecciona primero una fecha de inicio",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(40.dp))

            //Importe
            Text(stringResource(R.string.invoice_filter_price), fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
            Spacer(modifier = Modifier.height(16.dp))

            //Etiqueta precio
            PriceRangeSection(
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                minLimit = minLimit, // Uso el mínimo real
                maxLimit = maxLimit, // Uso el máximo real
                onRangeChange = { min, max -> events.onPriceRangeChange(min, max) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            //Estado
            StatusFilterSection(
                statusOptions = statusOptions,
                selectedStatuses = state.selectedStatuses,
                onStatusToggle = { events.onStatusToggle(it) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            //Botones final
            FilterActionButtons(
                onApply = { events.onApply() },
                onClear = { events.onClear() }
            )
        }
    }
}

/**
 * Diálogo personalizado para la selección de fechas
 * Permite configurar una fecha mínima seleccionable para validar rangos coherentes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    minDateStr: String? = null,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    //Convierto la fecha "Desde" a milisegundos
    val minDateMillis = remember(minDateStr) {
        if (!minDateStr.isNullOrEmpty()) {
            try {
                SimpleDateFormat("dd/MM/yyyy", getDefault()).parse(minDateStr)?.time
            } catch (e: Exception) { null }
        } else null
    }

    //Configuro el estado con la restricción
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                //Si minDateMillis es null, todo es seleccionable.
                //Si no, solo lo que sea mayor o igual
                return minDateMillis == null || utcTimeMillis >= minDateMillis
            }
        }
    )

    val selectedDate = datePickerState.selectedDateMillis?.let {
        val date = Date(it)
        val format = SimpleDateFormat("dd/MM/yyyy",getDefault())
        format.format(date)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDate.isNotEmpty()) onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK", color = GreenIberdrola, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar", color = GreenIberdrola,fontFamily = IberPangeaFamily)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                todayContentColor = GreenIberdrola,
                todayDateBorderColor = GreenIberdrola,
                selectedDayContainerColor = GreenIberdrola,
                selectedDayContentColor = Color.White
            )
        )
    }
}

/**
 * Vista previa de la pantalla de filtros con datos de ejemplo cargados
 */
@Preview(
    showBackground = true,
)
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