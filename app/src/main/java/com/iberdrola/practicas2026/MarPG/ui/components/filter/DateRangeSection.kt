package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

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
        Text(text = stringResource(R.string.filter_date_section_title), fontSize = 14.sp, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
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

@Composable
private fun DateField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontFamily = IberPangeaFamily,
                    color = Color.Gray,
                    lineHeight = 12.sp
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontFamily = IberPangeaFamily,
                    color = Color.Black,
                    lineHeight = 16.sp
                )
            } else {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontFamily = IberPangeaFamily,
                    color = Color.Gray
                )
            }
        }
        
        Icon(
            imageVector = Icons.Outlined.CalendarToday,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier
                .size(25.dp).padding(end = 4.dp)
                .align(Alignment.CenterEnd)
        )
        
        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomStart),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}