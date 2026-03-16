package com.iberdrola.practicas2026.MarPG.ui.utils

/** Formatea Double a moneda (ej: 10,50 €) */
fun Double.toCurrencyFormat(): String {
    //Formatea el Double con 2 decimales y cambia el punto por coma
    return String.format("%.2f", this).replace(".", ",") + " €"
}