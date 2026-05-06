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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.components.ErrorBanner
import com.iberdrola.practicas2026.MarPG.ui.components.InvoiceEmptyState
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ContractCard
import com.iberdrola.practicas2026.MarPG.ui.components.contract_selection.ShimmerElectronicInvoiceList
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectronicInvoiceSelectionScreen(
    viewModel: ElectronicInvoiceListViewModel,
    onBack: () -> Unit,
    onNavigate: (ElectronicInvoice) -> Unit
) {
    val state = viewModel.state
    val errorMessage = viewModel.errorMessage
    val pullToRefreshState = rememberPullToRefreshState()


    val remoteConfig = Firebase.remoteConfig

    LaunchedEffect(Unit) {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf("show_gas_contracts" to true)
        remoteConfig.setDefaultsAsync(defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            viewModel.updateGasAvailability(remoteConfig.getBoolean("show_gas_contracts"))
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    viewModel.updateGasAvailability(remoteConfig.getBoolean("show_gas_contracts"))
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {}
        })
    }


    val events = ElectronicInvoiceListEvents(
        onRetry = { viewModel.loadInvoices() },
        onElectronicInvoiceClick = { invoice ->
            onNavigate(invoice)
        }
    )

    Scaffold(
        containerColor = WhiteApp,
        topBar = {
            FilterTopBar(onBack = onBack)
        },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshInvoices() },
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = viewModel.isRefreshing,
                    containerColor = Color(0xFFF0F0F0),
                    color = GreenIberdrola,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                            errorMessage = errorMessage?.let { stringResource(it) },
                            events = events,
                            modifier = Modifier.navigationBarsPadding()
                        )
                    }

                    is ElectronicInvoiceListState.NoData -> {
                        InvoiceEmptyState(
                            message = errorMessage?.let { stringResource(it) },
                            onRefresh = events.onRetry
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ElectronicInvoiceSelectionContent(
    invoices: List<ElectronicInvoice>,
    errorMessage: String?,
    events: ElectronicInvoiceListEvents,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (errorMessage != null) {
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                ErrorBanner(message = errorMessage)
            }
        }

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

        Spacer(modifier = Modifier.height(24.dp))
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
