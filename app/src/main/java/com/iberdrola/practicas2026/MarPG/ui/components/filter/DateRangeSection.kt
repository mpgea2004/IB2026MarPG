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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola

@Composable
fun DateRangeSection(
    dateFrom: String,
    dateTo: String,
    onFromClick: () -> Unit,
    onToClick: () -> Unit
) {
    Column {
        Text("Por fecha", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DateField(
                label = "Desde",
                value = dateFrom,
                modifier = Modifier.weight(1f),
                onClick = onFromClick
            )
            DateField(
                label = "Hasta",
                value = dateTo,
                modifier = Modifier.weight(1f),
                onClick = onToClick
            )
        }
    }
}

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
            label = { Text(label, fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, null, tint = GreenIberdrola)
            },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.LightGray,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Black
            )
        )
    }
}