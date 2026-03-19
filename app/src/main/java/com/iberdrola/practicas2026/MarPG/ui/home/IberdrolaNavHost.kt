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
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceDetailFormScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceDetailInfoScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceEditEmailScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceOtpScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceSuccessFullGreenScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceViewModel
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
    // Flujo de Factura Electrónica
    const val ELECTRONIC_INVOICE_SELECTION = "electronic_invoice_selection"
    const val ELECTRONIC_INVOICE_DETAIL = "electronic_invoice_detail" // La de info (Modificar)
    const val ELECTRONIC_INVOICE_FORM = "electronic_invoice_form"     // La de activación (Legal)
    const val ELECTRONIC_INVOICE_EDIT_EMAIL = "electronic_invoice_edit" // La de cambiar email
    const val ELECTRONIC_INVOICE_OTP = "electronic_invoice_otp"
    const val ELECTRONIC_INVOICE_SUCCESS = "electronic_invoice_success"
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
                onNavigateToElectronicInvoice = { navController.navigate(Routes.ELECTRONIC_INVOICE_SELECTION) }
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

        /** 1. SELECCIÓN DE CONTRATO */
        composable(Routes.ELECTRONIC_INVOICE_SELECTION) { backStackEntry ->
            // Obtenemos el VM vinculado a esta entrada del backstack para que sea el "Padre"
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(backStackEntry)

            ElectronicInvoiceSelectionScreen(
                viewModel = hiltViewModel(), // VM de la lista
                onNavigate = { contrato ->
                    // --- PASO CLAVE: Guardar en el VM compartido ---
                    sharedViewModel.selectContract(contrato)

                    // Ahora navegamos sabiendo que el VM ya tiene los datos
                    if (contrato.isEnabled) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_DETAIL)
                    } else {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_FORM)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        /** 2. DETALLE / INFO (GAS) */
        composable(Routes.ELECTRONIC_INVOICE_DETAIL) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)

            ElectronicInvoiceDetailInfoScreen(
                viewModel = sharedViewModel,
                // Ahora state.selectedContract YA NO SERÁ NULL
                electronicInvoice = sharedViewModel.state.selectedContract,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) },
                onNavigateToSuccess = {navController.navigate(Routes.ELECTRONIC_INVOICE_SUCCESS)}
            )
        }

        /** 3. FORMULARIO ACTIVACIÓN (Legal + Email) */
        composable(Routes.ELECTRONIC_INVOICE_FORM) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            ElectronicInvoiceDetailFormScreen(
                viewModel = hiltViewModel(parentEntry),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.ELECTRONIC_INVOICE_OTP) }
            )
        }

        /** 4. MODIFICAR EMAIL */
        composable(Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            ElectronicInvoiceEditEmailScreen(
                viewModel = hiltViewModel(parentEntry),
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Routes.ELECTRONIC_INVOICE_OTP) }
            )
        }

        /** 5. VERIFICACIÓN OTP */
        composable(Routes.ELECTRONIC_INVOICE_OTP) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            ElectronicInvoiceOtpScreen(
                viewModel = hiltViewModel(parentEntry),
                onBack = { navController.popBackStack() },
                onNext = {
                    navController.navigate(Routes.ELECTRONIC_INVOICE_SUCCESS)
                }
            )
        }

        /** 6. PANTALLA DE ÉXITO */
        composable(Routes.ELECTRONIC_INVOICE_SUCCESS) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            val vm: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)

            ElectronicInvoiceSuccessFullGreenScreen(
                viewModel = vm,
                onFinish = {
                    // Limpiamos el flujo y volvemos al inicio (Home o Selección)
                    navController.popBackStack(Routes.HOME, false)
                }
            )
        }
    }
}