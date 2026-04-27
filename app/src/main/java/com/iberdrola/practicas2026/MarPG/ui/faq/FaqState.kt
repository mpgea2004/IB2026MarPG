package com.iberdrola.practicas2026.MarPG.ui.faq

data class FaqItem(
    val id: Int,
    val question: String,
    val answer: String
)

data class FaqState(
    val faqList: List<FaqItem> = emptyList(),
    val expandedItems: Set<Int> = emptySet()
)
