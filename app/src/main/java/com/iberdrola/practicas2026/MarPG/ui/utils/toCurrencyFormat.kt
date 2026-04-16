package com.iberdrola.practicas2026.MarPG.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun Double.toAnnotatedCurrencyFormat(
    amountSize: TextUnit,
    euroSize: TextUnit = (amountSize.value * 0.6).sp,
): AnnotatedString {
    val formattedAmount =  String.format("%.2f", this).replace(".", ",")
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = amountSize)){
            append(formattedAmount)
        }
        append(" ")
        withStyle(style = SpanStyle(fontSize = euroSize)){
            append("€")
        }
    }
}