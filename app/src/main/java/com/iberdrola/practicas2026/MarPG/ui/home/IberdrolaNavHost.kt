package com.iberdrola.practicas2026.MarPG.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iberdrola.practicas2026.MarPG.ui.factura_home.HomeScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen

object Routes {
    const val HOME = "home"
    const val INVOICE_LIST = "invoice_list"
}

@Composable
fun IberdrolaNavHost(navController: NavHostController) {
    //Declaro las variables de conteo
    //Pongo -1 o 0 para que la primera vez salga
    var contador by remember { mutableIntStateOf(0) }
    var objetivo by remember { mutableIntStateOf(0) }
    var showSheet by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToInvoices = { navController.navigate(Routes.INVOICE_LIST) },
                showFeedbackSheet = showSheet,
                onDismissSheet = {
                    objetivo = 1 //Si lo cierra sin más, que vuelva a salir a la próxima
                    contador = 0
                    showSheet = false
                },
                onOptionSelected = { tregua ->
                    objetivo = tregua //Aquí guarda el 3 o el 10
                    contador = 0
                    showSheet = false
                }
            )
        }

        composable(Routes.INVOICE_LIST) {
            InvoiceListScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    contador++ //Sumo uno al salir
                    if (contador >= objetivo) {
                        showSheet = true
                    }
                    navController.popBackStack()//Vuelvo a home
                },
                onNavigateToDetail = { /*Aun no implementado*/ }
            )
        }
    }
}