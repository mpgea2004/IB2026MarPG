package com.iberdrola.practicas2026.MarPG.ui.faq

import androidx.annotation.StringRes

data class FaqItem(
    val id: Int,
    @StringRes val question: Int,
    @StringRes val answer: Int
)

data class FaqState(
    val faqList: List<FaqItem> = emptyList(),
    val expandedItems: Set<Int> = emptySet()
)
