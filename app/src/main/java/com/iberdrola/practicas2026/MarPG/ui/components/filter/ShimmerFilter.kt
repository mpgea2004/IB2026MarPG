package com.iberdrola.practicas2026.MarPG.ui.components.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
fun ShimmerFilter(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhiteApp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(120.dp, 28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Box(modifier = Modifier.size(100.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp, 12.dp).clip(RoundedCornerShape(2.dp)).background(brush))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).alpha(0.3f).background(brush))
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp, 12.dp).clip(RoundedCornerShape(2.dp)).background(brush))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).alpha(0.3f).background(brush))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(modifier = Modifier.size(140.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp, 36.dp)
                .clip(RoundedCornerShape(8.dp))
                .alpha(0.15f)
                .background(brush)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).alpha(0.5f).background(brush))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(brush))
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(brush))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(40.dp, 14.dp).clip(RoundedCornerShape(2.dp)).background(brush))
            Box(modifier = Modifier.size(40.dp, 14.dp).clip(RoundedCornerShape(2.dp)).background(brush))
        }

        Spacer(modifier = Modifier.height(48.dp))

        Box(modifier = Modifier.size(120.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        Spacer(modifier = Modifier.height(24.dp))
        repeat(3) {
            Row(
                modifier = Modifier.padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(brush)) // Checkbox
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.size(160.dp, 16.dp).clip(RoundedCornerShape(4.dp)).background(brush)) // Label
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(140.dp, 18.dp)
                .clip(RoundedCornerShape(4.dp))
                .alpha(0.4f)
                .background(brush)
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}