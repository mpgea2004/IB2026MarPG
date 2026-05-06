package com.iberdrola.practicas2026.MarPG.ui.faq

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.iberdrola.practicas2026.MarPG.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FaqViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(FaqState())
        private set

    init {
        loadFaqData()
    }

    private fun loadFaqData() {
        val faqData = listOf(
            FaqItem(1, R.string.faq_q1, R.string.faq_a1),
            FaqItem(2, R.string.faq_q2, R.string.faq_a2),
            FaqItem(3, R.string.faq_q3, R.string.faq_a3),
            R.string.faq_q4.let { FaqItem(4, it, R.string.faq_a4) },
            FaqItem(5, R.string.faq_q5, R.string.faq_a5)
        )
        state = state.copy(faqList = faqData)
    }

    fun onToggleExpand(id: Int) {
        val currentExpanded = state.expandedItems
        val newExpanded = if (currentExpanded.contains(id)) {
            currentExpanded - id
        } else {
            currentExpanded + id
        }
        state = state.copy(expandedItems = newExpanded)
    }
    fun openContactSupport(context: Context) {
        val intent = Intent(ACTION_VIEW, Uri.parse("https://www.iberdrola.es/atencion-cliente"))
        context.startActivity(intent)
    }
}
