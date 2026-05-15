package com.iberdrola.practicas2026.MarPG.ui.components.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightRedIberdrola

@Composable
fun StatusBadge(status: InvoiceStatus) {
    val (backgroundColor, contentColor) = when (status) {
        InvoiceStatus.PAGADAS -> LightGreenIberdrola to GreenIberdrola
        InvoiceStatus.PENDIENTES_PAGO -> LightRedIberdrola to Color(0xFFD32F2F)
        InvoiceStatus.EN_TRAMITE ->
            Color(0xFFE3F2FD) to Color(0xFF1976D2)

        InvoiceStatus.ANULADAS ->
            Color(0xFFEEEEEE) to Color(0xFF616161)

        InvoiceStatus.CUOTA_FIJA ->
            Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        else -> {
            Color(0xFFF5F5F5) to Color(0xFF616161)
        }
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = when(status) {
                InvoiceStatus.PAGADAS -> stringResource(R.string.status_paid)
                InvoiceStatus.PENDIENTES_PAGO -> stringResource(R.string.status_pending)
                InvoiceStatus.ANULADAS -> stringResource(R.string.status_cancelled)
                InvoiceStatus.EN_TRAMITE -> stringResource(R.string.status_processing)
                InvoiceStatus.CUOTA_FIJA -> stringResource(R.string.status_fixed)
            },
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = IberPangeaFamily
        )
    }
}