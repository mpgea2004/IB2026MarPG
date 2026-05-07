package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola

@Composable
fun LoadingOverlay() {
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                
                val previousStatusLight = insetsController.isAppearanceLightStatusBars
                val previousNavLight = insetsController.isAppearanceLightNavigationBars
                
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
                
                onDispose {
                    insetsController.isAppearanceLightStatusBars = previousStatusLight
                    insetsController.isAppearanceLightNavigationBars = previousNavLight
                }
            } else {
                onDispose {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = true
            ) {},
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
