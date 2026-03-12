package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.ui.components.FeedbackSheetContent

//Esta pantalla la mejoraré
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInvoices: () -> Unit,
    showFeedbackSheet: Boolean,
    onDismissSheet: () -> Unit,
    onOptionSelected: (Int) -> Unit // Nueva función para pasar el 3 o el 10
) {
    val sheetState = rememberModalBottomSheetState()

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNavigateToInvoices,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008244))
            ) {
                Text("Ir a Mis Facturas")
            }
        }

        if (showFeedbackSheet) {
            ModalBottomSheet(
                onDismissRequest = { onDismissSheet() },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                // Aquí llamamos a tu componente de diseño
                FeedbackSheetContent(
                    onRatingClick = { onOptionSelected(10) }, //Si valora -> 10 veces
                    onLaterClick = { onOptionSelected(3) },//Si es "luego" -> 3 veces
                )
            }
        }
    }
}