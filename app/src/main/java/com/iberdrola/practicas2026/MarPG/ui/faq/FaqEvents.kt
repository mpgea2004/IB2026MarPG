package com.iberdrola.practicas2026.MarPG.ui.faq

data class FaqEvents(
    val onToggleExpand: (Int) -> Unit = {},
    val onBack: () -> Unit = {},
    val onContactSupport: () -> Unit = {}
)
