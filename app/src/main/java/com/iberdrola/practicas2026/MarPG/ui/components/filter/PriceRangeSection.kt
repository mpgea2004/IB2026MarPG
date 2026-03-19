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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola

/**
 * Sección de filtro por importe que utiliza un RangeSlider personalizado
 * Permite seleccionar un rango de precios entre un límite mínimo y máximo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeSection(
    minPrice: Float,
    maxPrice: Float,
    minLimit: Float = 0f,
    maxLimit: Float = 500f,
    onRangeChange: (Float, Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .background(LightGreenIberdrola, RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Text(
                text = stringResource(
                    id = R.string.filter_price_range,
                    minPrice.toInt(),
                    maxPrice.toInt()
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        RangeSlider(
            value = minPrice..maxPrice,
            onValueChange = { range ->
                val startValue = if (range.start < minLimit + 0.1f) minLimit else range.start
                val endValue = if (range.endInclusive > maxLimit - 0.1f) maxLimit else range.endInclusive
                onRangeChange(startValue, endValue)
            },
            valueRange = minLimit..maxLimit,
            steps = 0,
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

                    val startPos = width * ((rangeSliderState.activeRangeStart - minLimit) / (maxLimit - minLimit))
                    val endPos = width * ((rangeSliderState.activeRangeEnd - minLimit) / (maxLimit - minLimit))

                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(0f, trackHeight / 2),
                        end = Offset(width, trackHeight / 2),
                        strokeWidth = trackHeight,
                        cap = StrokeCap.Round
                    )

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.filter_price_limit, minLimit.toInt()), color = Color.Gray, fontSize = 14.sp)
            Text(stringResource(id = R.string.filter_price_limit, maxLimit.toInt()), color = Color.Gray, fontSize = 14.sp)
        }
    }
}
