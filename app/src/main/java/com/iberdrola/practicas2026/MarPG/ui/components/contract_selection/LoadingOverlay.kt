package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = GreenDarkIberdrola,
            trackColor = Color.LightGray.copy(alpha = 0.5f),
            strokeWidth = 6.dp,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
@Preview
fun LoadingOverlayPreview(){
    LoadingOverlay()
}
