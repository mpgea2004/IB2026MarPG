package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

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
            fontWeight = FontWeight.Bold,
            fontFamily = IberPangeaFamily
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            statusOptions.forEach { status ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onStatusToggle(status) }
                        .padding(end = 12.dp)
                ) {
                    Checkbox(
                        checked = selectedStatuses.contains(status),
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = GreenIberdrola,
                            uncheckedColor = GreenIberdrola
                        )
                    )

                    Text(
                        text = status,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 14.sp,
                        fontFamily = IberPangeaFamily
                    )
                }
            }
        }
    }
}
