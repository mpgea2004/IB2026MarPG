package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola

/**
 * Sección de filtros por estado de factura (Pagadas, Anuladas, etc.)
 * Genera dinámicamente una lista de checkboxes basada en las opciones proporcionadas
 */
@Composable
fun StatusFilterSection(
    statusOptions: List<String>,
    selectedStatuses: Set<String>,
    onStatusToggle: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.invoice_filter_state),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            statusOptions.forEach { status ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStatusToggle(status) }
                ) {
                    Checkbox(
                        checked = selectedStatuses.contains(status),
                        onCheckedChange = null, // null porque el click lo maneja la Row
                        colors = CheckboxDefaults.colors(
                            checkedColor = GreenIberdrola,
                            uncheckedColor = GreenIberdrola
                        )
                    )

                    Text(
                        text = status,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
