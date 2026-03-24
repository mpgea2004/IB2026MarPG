package com.iberdrola.practicas2026.MarPG.ui.factura_home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
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
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.home.DataSourceConfigSection
import com.iberdrola.practicas2026.MarPG.ui.components.home.ElectronicInvoiceCard
import com.iberdrola.practicas2026.MarPG.ui.components.home.FeedbackBottomSheet
import com.iberdrola.practicas2026.MarPG.ui.components.home.InvoiceNavigationCard
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey

/** Pantalla principal: acceso a facturas y configuración de origen de datos */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInvoices: (Boolean) -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val state = viewModel.state
    val events = viewModel.events
    val sheetState = rememberModalBottomSheetState()


    LaunchedEffect(state.isSheetVisible) {
        if (!state.isSheetVisible) {
            sheetState.hide()
        }
    }

    HomeContent(
        state = state,
        onNavigateToInvoices = {
            events.onNavigateToInvoices()
            onNavigateToInvoices(state.isCloudEnabled)
        },
        onNavigateToElectronicInvoice = {
            events.onNavigateToElectronicInvoice()
            onNavigateToElectronicInvoice()
        },
        onNavigateToProfile = {
            events.onProfileClick()
            onNavigateToProfile()
        },
        onToggleCloud = events.onToggleCloud,
        onSheetDismiss = events.onDismissFeedback,
        onSheetOptionSelected = events.onFeedbackOption,
        sheetState = rememberModalBottomSheetState()
    )
}

/** * Contenedor visual de la Home.
 * Separa la lógica de Scaffold y la estructura de la pantalla para facilitar Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    sheetState: SheetState,
    onNavigateToInvoices: () -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
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
            HomeHeader(userName = state.userName,
                onProfileClick = onNavigateToProfile)

            Spacer(modifier = Modifier.height(48.dp))

            InvoiceNavigationCard(onClick = onNavigateToInvoices)

            Spacer(modifier = Modifier.weight(1f))

            ElectronicInvoiceCard(onClick = onNavigateToElectronicInvoice)

            Spacer(modifier = Modifier.weight(1f))

            DataSourceConfigSection(
                isCloudEnabled = state.isCloudEnabled,
                onToggleCloud = onToggleCloud
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        if (state.isSheetVisible) {
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
private fun HomeHeader(userName: String,onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (userName.isNotEmpty()) {
                    stringResource(R.string.home_header_welcome, userName)
                } else {
                    stringResource(R.string.home_header_welcome)
                },
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenIberdrola
            )
            Text(
                text = stringResource(R.string.home_header_subtitle),
                fontSize = 16.sp,
                color = TextGrey,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = GreenIberdrola.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.home_profile_description),
                tint = GreenIberdrola,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}