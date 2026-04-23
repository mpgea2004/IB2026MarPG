package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            LightGray.copy(alpha = 0.6f),
            LightGray.copy(alpha = 0.2f),
            LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "shimmerTransition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "shimmerAnimation"
        )

        linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}
@Composable
fun ShimmerInvoiceList(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Box(modifier = Modifier.size(120.dp, 16.dp).background(brush))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.size(80.dp, 12.dp).background(brush))
                    }
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(brush))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(150.dp, 32.dp).background(brush))
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.size(200.dp, 14.dp).background(brush))
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightGray.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(100.dp, 24.dp).clip(RoundedCornerShape(12.dp)).background(brush))
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(26.dp)).background(brush).alpha(0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(120.dp, 20.dp).background(brush))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(brush))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    Box(modifier = Modifier.size(80.dp, 32.dp).clip(RoundedCornerShape(16.dp)).background(brush).alpha(0.5f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LightGray.copy(alpha = 0.3f)))
        }
        repeat(4) {
            ShimmerHistoricalItem(brush)
            Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(1.dp).background(LightGray.copy(alpha = 0.2f)))
        }
    }
}

@Composable
fun ShimmerHistoricalItem(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(140.dp, 16.dp).background(brush))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.size(100.dp, 14.dp).background(brush))
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.size(90.dp, 20.dp).clip(RoundedCornerShape(10.dp)).background(brush).alpha(0.6f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp, 16.dp).background(brush))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(brush).alpha(0.3f))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PreviewShimmerList() {
    val brush = shimmerBrush()
    ShimmerInvoiceList(brush = brush)
}