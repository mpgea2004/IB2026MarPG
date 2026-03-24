package com.iberdrola.practicas2026.MarPG.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.MarPG.ui.factura_home.HomeScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen

/** Definición de rutas de navegación de la aplicación */
object Routes {
    const val HOME = "home"
    /** Ruta del listado con argumento booleano para origen de datos */
    const val INVOICE_LIST = "invoice_list/{isCloud}"
}

/** Grafo de navegación principal que gestiona el estado del origen de datos (Nube/Local) */
@Composable
fun IberdrolaNavHost(navController: NavHostController) {

    //si esta a true uso mockoon, si no uso el json que tengo en assets
    var isCloudEnabled by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        /** Pantalla de inicio: permite configurar el origen y navegar */
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToInvoices = { navController.navigate("invoice_list/$isCloudEnabled") },
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = { isCloudEnabled = it },
            )
        }

        /** Pantalla de listado: recibe el parámetro isCloud para el ViewModel */
        composable(
            Routes.INVOICE_LIST,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType })) {
            InvoiceListScreen(
                viewModel = hiltViewModel(),
                onBack = {
                    navController.popBackStack()//Vuelvo a home
                },
                onNavigateToDetail = { /*Aun no implementado*/ }
            )
        }
    }
}