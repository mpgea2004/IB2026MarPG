package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.ui.utils.UiText

data class ConsumptionState(
    val isLoading: Boolean = true,
    val isConfigLoading: Boolean = true,
    val selectedType: ContractType = ContractType.LUZ,
    val chartData: List<Pair<String, Double>> = emptyList(),
    val comparisonMessage: UiText = UiText.DynamicString(""),
    val isPositiveTrend: Boolean = false,
    val isCloud: Boolean = false,
    val isGasEnabled: Boolean = true
)