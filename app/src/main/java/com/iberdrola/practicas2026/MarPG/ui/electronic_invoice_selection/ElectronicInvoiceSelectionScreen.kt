package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ShimmerElectronicInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ElectronicInvoiceSelectionScreen(
    viewModel: ElectronicInvoiceListViewModel,
    onBack: () -> Unit,
    onNavigate: (ElectronicInvoice) -> Unit
) {
    val state = viewModel.state

    val events = ElectronicInvoiceListEvents(
        onRetry = { viewModel.loadInvoices() },
        onElectronicInvoiceClick = { invoice ->
            viewModel.onElectronicInvoiceClick(invoice)
            onNavigate(invoice)
        }
    )

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack = onBack)
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (state) {
                is ElectronicInvoiceListState.Loading -> {
                    ShimmerElectronicInvoiceList(
                        brush = shimmerBrush(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is ElectronicInvoiceListState.Success -> {
                    ElectronicInvoiceSelectionContent(
                        invoices = state.contracts,
                        events = events
                    )
                }

                is ElectronicInvoiceListState.Error -> {
                    ErrorComponent(
                        message = state.message,
                        onRetry = events.onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ElectronicInvoiceSelectionContent(
    invoices: List<ElectronicInvoice>,
    events: ElectronicInvoiceListEvents,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        AnimateElectronicInvoiceItem(index = 0) {
            Text(
                text = stringResource(R.string.contract_selection_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = IberPangeaFamily,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))


        invoices.forEachIndexed { index, elecInvoice ->
            AnimateElectronicInvoiceItem(index = index + 1) {
                ContractCard(
                    elecInvoice = elecInvoice,
                    onClick = { events.onElectronicInvoiceClick(elecInvoice) }
                )
            }
        }
    }
}

@Composable
fun AnimateElectronicInvoiceItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 500, delayMillis = (index * 60).coerceAtMost(300))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 500, delayMillis = (index * 60).coerceAtMost(300)),
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
    }
}
