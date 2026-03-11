package com.iberdrola.practicas2026.MarPG

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListViewModel
import com.iberdrola.practicas2026.MarPG.ui.theme.IB2026MarPGTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IB2026MarPGTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos directamente a tu pantalla
                    val viewModel: InvoiceListViewModel = hiltViewModel()

                    InvoiceListScreen(
                        viewModel = viewModel,
                        onBack = { finish() }, // Si pulsa atrás, cierra la actividad
                        onNavigateToDetail = { invoice ->
                            // De momento no hace nada hasta que tengamos la otra pantalla
                        }
                    )
                }

            }
        }
    }
}
