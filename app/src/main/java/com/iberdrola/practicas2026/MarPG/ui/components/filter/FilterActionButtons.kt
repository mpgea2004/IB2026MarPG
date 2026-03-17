package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/** * Botones de acción para la pantalla de filtros
 * Incluye el botón principal de confirmación y el enlace para resetear los valores
 */
@Composable
fun FilterActionButtons(
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenIberdrola)
        ) {
            Text(stringResource(R.string.invoice_filter_button_apply), color = WhiteApp, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.invoice_filter_button_clear),
            textDecoration = TextDecoration.Underline,
            color = GreenIberdrola,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onClear() }
                .padding(bottom = 32.dp)
        )
    }
}