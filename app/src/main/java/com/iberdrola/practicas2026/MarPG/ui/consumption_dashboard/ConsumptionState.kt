package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType

data class ConsumptionState(
    val isLoading: Boolean = true,
    val selectedType: ContractType = ContractType.LUZ,
    val chartData: List<Pair<String, Double>> = emptyList(),
    val comparisonMessage: String = "",
    val isPositiveTrend: Boolean = false,
    val isCloud: Boolean = false
)