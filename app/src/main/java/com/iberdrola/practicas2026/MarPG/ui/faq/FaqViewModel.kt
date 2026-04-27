package com.iberdrola.practicas2026.MarPG.ui.faq

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
            FaqItem(1, "¿Cómo puedo ver mis facturas?", "Puedes ver tus facturas pulsando en la tarjeta 'Mis Facturas' de la pantalla principal. Allí podrás filtrar por fecha, importe o estado."),
            FaqItem(2, "¿Qué es el modo Nube?", "El modo Nube te permite sincronizar tus datos reales con el servidor. Si lo desactivas, la aplicación utilizará datos locales de demostración."),
            FaqItem(3, "¿Cómo activo la factura electrónica?", "En la pantalla de inicio, selecciona 'Factura Electrónica'. Podrás elegir qué contratos quieres pasar a digital y configurar tu email de recepción."),
            FaqItem(4, "¿Dónde veo mi consumo energético?", "Dentro del listado de facturas, encontrarás un icono de gráfico en la parte superior derecha. Al pulsarlo, accederás al análisis detallado de tu consumo."),
            FaqItem(5, "¿Cómo actualizo mis datos personales?", "Pulsa en el icono de usuario en la esquina superior derecha de la pantalla de inicio para acceder a tu perfil y editar tu información.")
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
