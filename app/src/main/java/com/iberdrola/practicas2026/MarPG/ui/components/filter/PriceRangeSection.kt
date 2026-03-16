package com.iberdrola.practicas2026.MarPG.ui.components.filter

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeSection(
    minPrice: Float,
    maxPrice: Float,
    minLimit: Float = 0f,
    maxLimit: Float = 500f,
    onRangeChange: (Float, Float) -> Unit
){
    @Composable
    fun PriceRangeSection(
        minPrice: Float,
        maxPrice: Float,
        minLimit: Float = 0f,
        maxLimit: Float = 500f,
        onRangeChange: (Float, Float) -> Unit
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Badge con el precio actual
            Box(
                modifier = Modifier
                    .background(LightGreenIberdrola, RoundedCornerShape(4.dp))
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${minPrice.toInt()}€ - ${maxPrice.toInt()}€",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            RangeSlider(
                value = minPrice..maxPrice,
                onValueChange = { range ->
                    onRangeChange(range.start, range.endInclusive)
                },
                valueRange = minLimit..maxLimit,
                modifier = Modifier.fillMaxWidth(),
                startThumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(GreenIberdrola, shape = CircleShape)
                    )
                },
                endThumb = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(GreenIberdrola, shape = CircleShape)
                    )
                },
                track = { rangeSliderState ->
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    ) {
                        val trackHeight = size.height
                        val width = size.width

                        // Uso los límites para calcular la posición proporcional
                        val startPos = width * ((rangeSliderState.activeRangeStart - minLimit) / (maxLimit - minLimit))
                        val endPos = width * ((rangeSliderState.activeRangeEnd - minLimit) / (maxLimit - minLimit))

                        // Línea Inactiva (fondo gris)
                        drawLine(
                            color = Color(0xFFE0E0E0),
                            start = Offset(0f, trackHeight / 2),
                            end = Offset(width, trackHeight / 2),
                            strokeWidth = trackHeight,
                            cap = StrokeCap.Round
                        )

                        // Línea Activa (verde Iberdrola)
                        drawLine(
                            color = GreenIberdrola,
                            start = Offset(startPos, trackHeight / 2),
                            end = Offset(endPos, trackHeight / 2),
                            strokeWidth = trackHeight,
                            cap = StrokeCap.Round
                        )
                    }
                }
            )

            // Etiquetas de los extremos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${minLimit.toInt()} €", color = Color.Gray, fontSize = 14.sp)
                Text("${maxLimit.toInt()} €", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}