package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType

data class ConsumptionDashboardEvents(
    val onBack: () -> Unit,
    val onTypeSelected: (ContractType) -> Unit
)