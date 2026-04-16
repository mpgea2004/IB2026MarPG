package com.iberdrola.practicas2026.MarPG.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/** Switch para alternar entre Mockoon (Nube) y Assets (Local) */
@Composable
fun DataSourceConfigSection(
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.home_footer_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp),
            fontFamily = IberPangeaFamily
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteApp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isCloudEnabled) stringResource(R.string.home_footer_typeS) else stringResource(R.string.home_footer_typeL),
                        fontWeight = FontWeight.Bold,
                        color = if (isCloudEnabled) GreenIberdrola else Color.DarkGray,
                        fontFamily = IberPangeaFamily
                    )
                    Text(
                        text = stringResource(R.string.home_footer_subtitle),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = IberPangeaFamily
                    )
                }
                Switch(
                    checked = isCloudEnabled,
                    onCheckedChange = onToggleCloud,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = WhiteApp,
                        checkedTrackColor = GreenIberdrola
                    )
                )
            }
        }
    }
}
