package com.iberdrola.practicas2026.MarPG.ui.components.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

@Composable
fun InvoiceStepper(status: InvoiceStatus, modifier: Modifier = Modifier) {
    val steps = listOf("Emitida", "En trámite", "Pagada")
    
    val currentStep = when (status) {
        InvoiceStatus.PAGADAS -> 3
        InvoiceStatus.CUOTA_FIJA -> 3
        InvoiceStatus.EN_TRAMITE -> 2
        InvoiceStatus.PENDIENTES_PAGO -> 1
        InvoiceStatus.ANULADAS -> -1 
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
            val y = size.height / 2
            val itemWidth = size.width / steps.size
            val startX = itemWidth / 2
            val endX = size.width - startX
            
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 2.dp.toPx()
            )
            
            if (currentStep > 1 && status != InvoiceStatus.ANULADAS) {
                val progressFraction = (currentStep - 1).toFloat() / (steps.size - 1).toFloat()
                drawLine(
                    color = GreenIberdrola,
                    start = Offset(startX, y),
                    end = Offset(startX + (endX - startX) * progressFraction, y),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            steps.forEachIndexed { index, title ->
                val stepNumber = index + 1
                val isCompleted = currentStep >= stepNumber
                val isActive = currentStep == stepNumber || (currentStep == -1 && index == 0)
                val isAnulada = status == InvoiceStatus.ANULADAS

                StepItem(
                    title = title,
                    isCompleted = isCompleted,
                    isActive = isActive,
                    isAnulada = isAnulada && index > 0,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StepItem(
    title: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isAnulada: Boolean,
    modifier: Modifier = Modifier
) {
    val circleColor by animateColorAsState(
        targetValue = when {
            isAnulada -> Color.Red.copy(alpha = 0.1f)
            isCompleted -> GreenIberdrola
            isActive -> GreenIberdrola.copy(alpha = 0.2f)
            else -> Color.LightGray.copy(alpha = 0.4f)
        }, label = ""
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp), tint = Color.White)
            } else if (isAnulada) {
                Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = Color.Red)
            } else {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isActive) GreenIberdrola else Color.Gray))
            }
        }
        
        Text(
            text = title,
            fontFamily = IberPangeaFamily,
            fontSize = 11.sp,
            fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal,
            color = if (isAnulada) Color.Red else if (isCompleted || isActive) Color.Black else Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
