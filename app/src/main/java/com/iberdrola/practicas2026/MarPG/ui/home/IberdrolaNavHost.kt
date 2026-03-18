package com.iberdrola.practicas2026.MarPG.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection.ElectronicInvoiceSelectionScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_home.HomeScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListViewModel

/** Definición de rutas de navegación de la aplicación */
object Routes {
    const val HOME = "home"
    /** Ruta del listado con argumento booleano para origen de datos */
    const val INVOICE_LIST = "invoice_list/{isCloud}"
    const val FILTER = "filter"
    const val ELECTRONIC_INVOICE = "electronic_invoice"
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
                onNavigateToElectronicInvoice = { navController.navigate(Routes.ELECTRONIC_INVOICE) }
            )
        }

        /** Pantalla de listado: recibe el parámetro isCloud para el ViewModel */
        composable(
            Routes.INVOICE_LIST,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType })
        ) { backStackEntry ->
            //Creo el ViewModel ligado a esta ruta
            val invoiceListViewModel: InvoiceListViewModel = hiltViewModel(backStackEntry)
            InvoiceListScreen(
                viewModel = invoiceListViewModel,
                onBack = {
                    navController.popBackStack()//Vuelvo a home
                },
                onNavigateToFilters = { navController.navigate(Routes.FILTER) }
            )
        }

        /** Pantalla de filtros */
        composable(Routes.FILTER) { backStackEntry->
            //Recupero la instancia del ViewModel de la lista que ya existe
            //Uso "navController.getBackStackEntry" con la ruta de la lista
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.INVOICE_LIST)
            }
            val invoiceListViewModel: InvoiceListViewModel = hiltViewModel(parentEntry)

            FilterScreen(
                listViewModel = invoiceListViewModel,
                filterViewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        /** * Pantalla de Factura Electrónica (Selección de Contrato)*/
        composable(Routes.ELECTRONIC_INVOICE) {
            ElectronicInvoiceSelectionScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() },
                onNavigate = { }
            )
        }
    }
}