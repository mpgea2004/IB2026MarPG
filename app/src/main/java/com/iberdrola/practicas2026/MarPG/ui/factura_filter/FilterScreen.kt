package com.iberdrola.practicas2026.MarPG.ui.factura_filter

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp


@Composable
fun FilterScreen(
    onBack: () -> Unit,
    onApply: (FilterState) -> Unit,
    initialState: FilterState = FilterState()
) {

    var currentState by remember { mutableStateOf(initialState) }

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack)
        }
    ) { padding ->

        FilterContent(
            modifier = Modifier.padding(padding).fillMaxSize(),
            state = currentState,
            onStateChange = { currentState = it },
            onApply = { onApply(currentState) },
            onClear = { currentState = FilterState() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = GreenIberdrola)
        Text(stringResource(R.string.invoice_list_back), color = GreenIberdrola, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterContent(
    modifier: Modifier = Modifier,
    state: FilterState,
    onStateChange: (FilterState) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    var priceRange by remember { mutableStateOf(0f..200f) }

    val statusOptions = listOf(
        "Pagadas",
        "Pendientes de Pago",
        "En trámite de cobro",
        "Anuladas",
        "Cuota Fija"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Título principal
        Text(
            text = "Filtrar",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sección fecha
        Text("Por fecha", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            //Desde
            TextField(
                value = state.dateFrom,
                onValueChange = { onStateChange(state.copy(dateFrom = it)) },
                label = { Text("Desde", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, null, tint = GreenIberdrola)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenIberdrola,
                    unfocusedIndicatorColor = Color.LightGray,
                )
            )

            //Hasta
            TextField(
                value = state.dateTo,
                onValueChange = { onStateChange(state.copy(dateTo = it)) },
                label = { Text("Hasta", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    Icon(Icons.Default.CalendarToday, null, tint = GreenIberdrola)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = GreenIberdrola,
                    unfocusedIndicatorColor = Color.LightGray,
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        //Importe
        Text("Por un importe", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        //Etiqueta precio
        Box(
            modifier = Modifier
                .background(LightGreenIberdrola, RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Text(
                text = "${priceRange.start.toInt()}€ - ${priceRange.endInclusive.toInt()}€",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        RangeSlider(
            value = priceRange,
            onValueChange = { priceRange = it },
            valueRange = 0f..200f,
            modifier = Modifier.fillMaxWidth(),
            startThumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(GreenIberdrola, shape = CircleShape)
                )
            },
            endThumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(GreenIberdrola, shape = CircleShape)
                )
            },
            track = { rangeSliderState ->
                //He tenido dibujado la línea para que se una a los puntos
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                ) {
                    val trackHeight = size.height
                    val width = size.width

                    val startPos = width * ((rangeSliderState.activeRangeStart - 0f) / (200f - 0f))
                    val endPos = width * ((rangeSliderState.activeRangeEnd - 0f) / (200f - 0f))

                    //Línea Inactiva(gris)
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(0f, trackHeight / 2),
                        end = Offset(width, trackHeight / 2),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

                    //Línea Activa(Verde)
                    drawLine(
                        color = GreenIberdrola,
                        start = Offset(startPos, trackHeight / 2),
                        end = Offset(endPos, trackHeight / 2),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${priceRange.start.toInt()} €", color = Color.Gray, fontSize = 14.sp)
            Text("${priceRange.endInclusive.toInt()} €", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        //Estado
        Text("Por estado", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            statusOptions.forEach { status ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val newSet =
                                if (state.selectedStatuses.contains(status))
                                    state.selectedStatuses - status
                                else
                                    state.selectedStatuses + status

                            onStateChange(state.copy(selectedStatuses = newSet))
                        }
                ) {

                    Checkbox(
                        checked = state.selectedStatuses.contains(status),
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = GreenIberdrola,
                            uncheckedColor = GreenIberdrola
                        )
                    )

                    Text(
                        text = status,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        //Botones final
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenIberdrola)
        ) {
            Text("Aplicar filtros", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Borrar filtros",
            textDecoration = TextDecoration.Underline,
            color = GreenIberdrola,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onClear() }
                .padding(bottom = 32.dp)
        )
    }
}
@Preview(
    showBackground = true,
)
@Composable
fun FilterScreenFilledPreview() {

    com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme {

        FilterScreen(
            onBack = {},
            onApply = {},
            initialState = FilterState(
                dateFrom = "01/01/2024",
                dateTo = "01/12/2024",
                selectedStatuses = setOf(
                    "Pagadas",
                    "Pendientes de Pago"
                )
            )
        )
    }
}