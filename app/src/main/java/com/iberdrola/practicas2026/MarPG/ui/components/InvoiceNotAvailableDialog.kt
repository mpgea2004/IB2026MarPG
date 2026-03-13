package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R

/**
 * Diálogo de alerta para informar que una factura no está disponible para su visualización
 * Se utiliza como fallback cuando el usuario intenta interactuar con un elemento sin detalle
 *
 * @param onDismiss Callback que se ejecuta al cerrar el diálogo o pulsar el botón de confirmación
 */

@Composable
fun InvoiceNotAvailableDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.invoice_not_available_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.invoice_not_available_message),
                fontSize = 16.sp,
                color = Color.Gray
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.invoice_not_available_button),
                    color = Color(0xFF008244),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
