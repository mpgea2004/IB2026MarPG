package com.iberdrola.practicas2026.MarPG.ui.factura_home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.FeedbackSheetContent
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/** Pantalla principal: acceso a facturas y configuración de origen de datos */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInvoices: () -> Unit,
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    // Compruebo el estado al entrar o volver a la pantalla
    LaunchedEffect(Unit) {
        viewModel.checkPendingFeedback()
    }

    Scaffold(
        containerColor = Color(0xFFF7F9F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            HomeHeader()

            Spacer(modifier = Modifier.height(48.dp))

            InvoiceNavigationCard(onClick = onNavigateToInvoices)

            Spacer(modifier = Modifier.weight(1f))

            DataSourceConfigSection(
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = onToggleCloud
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        //Conecto con el estado del ViewModel
        if (viewModel.isSheetVisible) {
            FeedbackBottomSheet(
                sheetState = sheetState,
                //Si el usuario cierra el sheet sin elegir, aplico tregua de 1
                onDismiss = { viewModel.onOptionSelected(1) },
                onOptionSelected = { tregua -> viewModel.onOptionSelected(tregua) }
            )
        }
    }
}

/** Título y subtítulo de bienvenida */
@Composable
private fun HomeHeader() {
    Column {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.home_header_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenIberdrola
        )
        Text(
            text = stringResource(R.string.home_header_subtitle),
            fontSize = 16.sp,
            color = TextGrey,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Tarjeta de acceso al listado de facturas */
@Composable
private fun InvoiceNavigationCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteApp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = LightGreenIberdrola
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = GreenIberdrola,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.home_invoices_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    stringResource(R.string.home_invoices_subtitle),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/** Switch para alternar entre Mockoon (Nube) y Assets (Local) */
@Composable
private fun DataSourceConfigSection(
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.home_footer_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isCloudEnabled) stringResource(R.string.home_footer_typeS) else stringResource(R.string.home_footer_typeL),
                        fontWeight = FontWeight.Bold,
                        color = if (isCloudEnabled) Color(0xFF008244) else Color.DarkGray
                    )
                    Text(
                        text = stringResource(R.string.home_footer_subtitle),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isCloudEnabled,
                    onCheckedChange = onToggleCloud,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF008244)
                    )
                )
            }
        }
    }
}

/** BottomSheet para encuesta: carita (10) o luego (3) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (Int) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        FeedbackSheetContent(
            onRatingClick = { onOptionSelected(10) },//Si dice carita, tregua de 10
            onLaterClick = { onOptionSelected(3) },//Si dice más tarde, tregua de 3
        )
    }
}