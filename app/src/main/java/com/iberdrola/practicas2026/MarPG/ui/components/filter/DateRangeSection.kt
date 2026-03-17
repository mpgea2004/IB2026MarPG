package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R

/** * Sección de filtros por rango de fechas.
 * Permite al usuario abrir selectores de fecha para los límites "Desde" y "Hasta".
 */
@Composable
fun DateRangeSection(
    dateFrom: String,
    dateTo: String,
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Column {
        Text(text = stringResource(R.string.filter_date_section_title), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DateField(
                label = stringResource(R.string.filter_date_from_label),
                value = dateFrom,
                modifier = Modifier.weight(1f),
                onClick = onFromClick,
            )
            DateField(
                label = stringResource(R.string.filter_date_to_label),
                value = dateTo,
                modifier = Modifier.weight(1f),
                onClick = onToClick,
            )
        }
    }
}

/** * Campo de texto personalizado que actúa como botón para abrir el DatePicker
 * Se marca como 'disabled' y 'readOnly' para evitar la escritura manual y usar el estilo visual deseado
 */
@Composable
private fun DateField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier.clickable { onClick() }) {
        TextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            enabled = false,
            label = { Text(label, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Outlined.CalendarToday, null, tint = Color.DarkGray)
            },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )
    }
}