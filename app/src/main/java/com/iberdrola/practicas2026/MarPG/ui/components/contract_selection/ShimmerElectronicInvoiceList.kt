package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ShimmerElectronicInvoiceList(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhiteApp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .size(240.dp, 28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(24.dp))

        repeat(2) {
            ShimmerContractCard(brush)
        }
    }
}

@Composable
private fun ShimmerContractCard(brush: Brush) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(brush))
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(100.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.size(180.dp, 12.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            }

            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .alpha(0.3f)
                    .background(brush)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0.1f)
                .background(brush)
        )
    }
}