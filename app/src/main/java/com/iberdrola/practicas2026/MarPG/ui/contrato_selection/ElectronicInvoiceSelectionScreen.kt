package com.iberdrola.practicas2026.MarPG.ui.contrato_selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ContractCard
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ErrorComponent
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

/**
 * Orquestador de la pantalla de selección de contrato.
 * Maneja la lógica de navegación y los datos del ViewModel.
 */
@Composable
fun ElectronicInvoiceSelectionScreen(
    viewModel: ElectronicInvoiceListViewModel,
    onBack: () -> Unit,
    onNavigate: (ElectronicInvoice) -> Unit
) {
    val state = viewModel.state

    when (state) {
        is ElectronicInvoiceListState.Loading -> { /* ... */ }

        is ElectronicInvoiceListState.Success -> {
            ElectronicInvoiceSelectionContent(
                invoices = state.contracts,
                onBack = onBack,
                onInvoiceClick = { invoice ->
                    // Llamas directamente a la función del ViewModel
                    viewModel.onElectronicInvoiceClick(invoice)
                    onNavigate(invoice)
                }
            )
        }

        is ElectronicInvoiceListState.Error -> {
            ErrorComponent(
                message = state.message,
                onRetry = { viewModel.loadInvoices() } // Llamada directa a la función del ViewModel
            )
        }
    }
}

/**
 * UI pura de la pantalla de selección.
 */
@Composable
fun ElectronicInvoiceSelectionContent(
    invoices: List<ElectronicInvoice>,
    onBack: () -> Unit,
    onInvoiceClick: (ElectronicInvoice) -> Unit
) {
    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.contract_selection_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))


            invoices.forEach { elecInvoice ->
                ContractCard(
                    elecInvoice = elecInvoice,
                    onClick = { onInvoiceClick(elecInvoice) }
                )
            }
        }
    }
}