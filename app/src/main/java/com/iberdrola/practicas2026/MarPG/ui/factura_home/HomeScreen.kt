package com.iberdrola.practicas2026.MarPG.ui.factura_home


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.home.DataSourceConfigSection
import com.iberdrola.practicas2026.MarPG.ui.components.home.FeedbackBottomSheet
import com.iberdrola.practicas2026.MarPG.ui.components.home.InvoiceNavigationCard
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey

/** Pantalla principal: acceso a facturas y configuración de origen de datos */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInvoices: () -> Unit,
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    /** Estado que controla la animación y visibilidad del ModalBottomSheet */
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current


    LaunchedEffect(viewModel.isSheetVisible) {
        if (!viewModel.isSheetVisible) {
            sheetState.hide()
        }
    }

    HomeContent(
        isCloudEnabled = isCloudEnabled,
        isSheetVisible = viewModel.isSheetVisible,
        sheetState = sheetState,
        onNavigateToInvoices = onNavigateToInvoices,
        onToggleCloud = onToggleCloud,
        onSheetDismiss = { viewModel.onOptionSelected(1) },
        onSheetOptionSelected = { tregua -> viewModel.onOptionSelected(tregua)
            if (tregua == 10) {
                Toast.makeText(
                    context,
                    context.getString(R.string.feedback_thanks_title),
                    Toast.LENGTH_SHORT
                ).show()
            }}
    )
}

/** * Contenedor visual de la Home.
 * Separa la lógica de Scaffold y la estructura de la pantalla para facilitar Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isCloudEnabled: Boolean,
    isSheetVisible: Boolean,
    sheetState: SheetState,
    onNavigateToInvoices: () -> Unit,
    onToggleCloud: (Boolean) -> Unit,
    onSheetDismiss: () -> Unit,
    onSheetOptionSelected: (Int) -> Unit
) {

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
        if (isSheetVisible) {
            FeedbackBottomSheet(
                sheetState = sheetState,
                onDismiss = onSheetDismiss,
                onOptionSelected = onSheetOptionSelected
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