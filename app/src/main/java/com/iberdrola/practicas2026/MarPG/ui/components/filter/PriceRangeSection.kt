package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.utils.formatPrice
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeSection(
    minPrice: Float,
    maxPrice: Float,
    minLimit: Float = 0f,
    maxLimit: Float = 500f,
    onRangeChange: (Float, Float) -> Unit,
    onDecimalChange: (Float, Float) -> Unit = { _, _ -> }
) {
    val floorMin = floor(minPrice)
    val floorMax = floor(maxPrice)
    val limitFloorMin = floor(minLimit)
    val limitFloorMax = floor(maxLimit)
    
    val isSingleUnitRange = (floorMax - floorMin) <= 1f
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .background(LightGreenIberdrola, RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            val priceText = "${minPrice.formatPrice()}€ - ${maxPrice.formatPrice()}€"
            Text(
                text = priceText,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = IberPangeaFamily,
                color = Color.Black
            )
        }

        RangeSlider(
            value = floorMin..floorMax,
            onValueChange = { range ->
                var newStart = range.start
                var newEnd = range.endInclusive
                val minDistance = 1f

                val startDiff = abs(newStart - floorMin)
                val endDiff = abs(newEnd - floorMax)
                
                if (limitFloorMax - limitFloorMin >= minDistance) {
                    if (startDiff > endDiff) {
                        if (newStart > floorMax - minDistance) {
                            newStart = (floorMax - minDistance).coerceAtLeast(limitFloorMin)
                        }
                        newEnd = floorMax
                    } else if (endDiff > startDiff) {
                        if (newEnd < floorMin + minDistance) {
                            newEnd = (floorMin + minDistance).coerceAtMost(limitFloorMax)
                        }
                        newStart = floorMin
                    }
                }
                onRangeChange(newStart, newEnd)
            },
            valueRange = limitFloorMin..limitFloorMax,
            steps = if (limitFloorMax > limitFloorMin) (limitFloorMax - limitFloorMin).toInt() - 1 else 0,
            modifier = Modifier.fillMaxWidth(),
            startThumb = { PriceThumb() },
            endThumb = { PriceThumb() },
            track = { rangeSliderState ->
                PriceTrack(
                    activeStart = rangeSliderState.activeRangeStart,
                    activeEnd = rangeSliderState.activeRangeEnd,
                    min = limitFloorMin,
                    max = limitFloorMax
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${minLimit.formatPrice()}€", 
                color = Color.Gray, 
                fontSize = 14.sp, 
                fontFamily = IberPangeaFamily
            )
            Text(
                text = "${maxLimit.formatPrice()}€", 
                color = Color.Gray, 
                fontSize = 14.sp, 
                fontFamily = IberPangeaFamily
            )
        }

        AnimatedVisibility(
            visible = isSingleUnitRange,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "Ajuste de céntimos (mín. 0.10€ de diferencia)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = IberPangeaFamily,
                    color = GreenIberdrola
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val base = floorMin
                
                val absoluteMinForDecimals = if (base == limitFloorMin) minLimit else base
                val absoluteMaxForDecimals = if (base + 1f >= limitFloorMax) maxLimit else base + 1f
                
                val localMin = absoluteMinForDecimals - base
                val localMax = absoluteMaxForDecimals - base
                
                val currentMinDec = (minPrice - base).coerceIn(localMin, localMax)
                val currentMaxDec = (maxPrice - base).coerceIn(localMin, localMax)
                
                RangeSlider(
                    value = currentMinDec..currentMaxDec,
                    onValueChange = { range ->
                        val minDistance = 0.10f
                        var newStart = range.start
                        var newEnd = range.endInclusive

                        val startDiff = abs(newStart - currentMinDec)
                        val endDiff = abs(newEnd - currentMaxDec)

                        if (startDiff > endDiff) {
                            if (newStart > currentMaxDec - minDistance) {
                                newStart = (currentMaxDec - minDistance).coerceAtLeast(localMin)
                            }
                            newEnd = currentMaxDec
                        } else if (endDiff > startDiff) {
                            if (newEnd < currentMinDec + minDistance) {
                                newEnd = (currentMinDec + minDistance).coerceAtMost(localMax)
                            }
                            newStart = currentMinDec
                        }

                        onDecimalChange(newStart, newEnd)
                    },
                    valueRange = localMin..localMax,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth(),
                    startThumb = { PriceThumb(size = 18.dp) },
                    endThumb = { PriceThumb(size = 18.dp) },
                    track = { rangeSliderState ->
                        PriceTrack(
                            activeStart = rangeSliderState.activeRangeStart,
                            activeEnd = rangeSliderState.activeRangeEnd,
                            min = localMin,
                            max = localMax
                        )
                    }
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(base + localMin).formatPrice()}€",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = IberPangeaFamily
                    )
                    Text(
                        text = "${(base + localMax).formatPrice()}€",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = IberPangeaFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceThumb(size: androidx.compose.ui.unit.Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(GreenIberdrola, shape = CircleShape)
    )
}

@Composable
private fun PriceTrack(activeStart: Float, activeEnd: Float, min: Float, max: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
    ) {
        val trackHeight = size.height
        val width = size.width
        val range = max - min

        val startPos = if (range > 0) width * ((activeStart - min) / range) else 0f
        val endPos = if (range > 0) width * ((activeEnd - min) / range) else width

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
