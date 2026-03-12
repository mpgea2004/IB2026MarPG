package com.iberdrola.practicas2026.MarPG.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.MarPG.ui.factura_home.HomeScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen

object Routes {
    const val HOME = "home"
    const val INVOICE_LIST = "invoice_list/{isCloud}" //La ruta acepta el argumento para saber si los datos vienen de mockoon o no
}

@Composable
fun IberdrolaNavHost(navController: NavHostController) {
    //Declaro las variables de conteo
    //Pongo -1 o 0 para que la primera vez salga
    var feedbackCount by remember { mutableIntStateOf(0) }
    var feedbackTarget by remember { mutableIntStateOf(0) }
    var isSheetVisible by remember { mutableStateOf(false) }
    //si esta a true uso mockoon, si no uso el json que tengo en assets
    var isCloudEnabled by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToInvoices = { navController.navigate("invoice_list/$isCloudEnabled") },
                showFeedbackSheet = isSheetVisible,
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = { isCloudEnabled = it },
                onDismissSheet = {
                    feedbackTarget = 1 //Si lo cierra sin más, que vuelva a salir a la próxima
                    feedbackCount = 0
                    isSheetVisible = false
                },
                onOptionSelected = { tregua ->
                    feedbackTarget = tregua //Aquí guarda el 3 o el 10
                    feedbackCount = 0
                    isSheetVisible = false
                }
            )
        }

        composable(
            Routes.INVOICE_LIST,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType })) {
            InvoiceListScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    feedbackCount++ //Sumo uno al salir
                    if (feedbackCount >= feedbackTarget) {
                        isSheetVisible = true
                    }
                    navController.popBackStack()//Vuelvo a home
                },
                onNavigateToDetail = { /*Aun no implementado*/ }
            )
        }
    }
}