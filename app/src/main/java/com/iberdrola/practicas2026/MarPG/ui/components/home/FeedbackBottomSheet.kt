package com.iberdrola.practicas2026.MarPG.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.ui.components.FeedbackSheetContent
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (Int) -> Unit,
    onDontAskAgain: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhiteApp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(androidx.compose.ui.graphics.Color.LightGray)
                        .clickable { onDismiss() }
                )
            }
        }
    ) {
        FeedbackSheetContent(
            onRatingClick = { onOptionSelected(10) },
            onLaterClick = { onOptionSelected(3) },
            onDontAskAgainClick = onDontAskAgain
        )
    }
}