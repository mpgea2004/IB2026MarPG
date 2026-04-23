package com.iberdrola.practicas2026.MarPG.ui.components.detail

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ShimmerInvoiceDetail(brush: Brush) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteApp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp, 14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.size(180.dp, 40.dp).clip(RoundedCornerShape(8.dp)).background(brush))
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.size(140.dp, 20.dp).clip(RoundedCornerShape(12.dp)).background(brush))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(3) {
                ShimmerInfoCard(brush)
            }
        }
    }
}

@Composable
private fun ShimmerInfoCard(brush: Brush) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(brush))
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(120.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.width(200.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        }
    }
}