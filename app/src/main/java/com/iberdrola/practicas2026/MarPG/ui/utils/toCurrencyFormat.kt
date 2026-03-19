package com.iberdrola.practicas2026.MarPG.ui.utils

fun Double.toCurrencyFormat(): String {
    return String.format("%.2f", this).replace(".", ",") + " €"
}