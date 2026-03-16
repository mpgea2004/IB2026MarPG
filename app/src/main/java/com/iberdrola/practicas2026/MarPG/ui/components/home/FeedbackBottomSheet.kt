package com.iberdrola.practicas2026.MarPG.ui.components.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.iberdrola.practicas2026.MarPG.ui.components.FeedbackSheetContent
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/** BottomSheet para encuesta: carita (10) o luego (3) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (Int) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhiteApp
    ) {
        FeedbackSheetContent(
            onRatingClick = { onOptionSelected(10) },//Si dice carita, tregua de 10
            onLaterClick = { onOptionSelected(3) },//Si dice más tarde, tregua de 3
        )
    }
}