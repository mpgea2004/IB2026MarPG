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

object Routes {
    const val HOME = "home"
    const val INVOICE_LIST = "invoice_list/{isCloud}"
    const val FILTER = "filter"
    const val ELECTRONIC_INVOICE_SELECTION = "electronic_invoice_selection"
    const val ELECTRONIC_INVOICE_DETAIL = "electronic_invoice_detail"
    const val ELECTRONIC_INVOICE_FORM = "electronic_invoice_form"
    const val ELECTRONIC_INVOICE_EDIT_EMAIL = "electronic_invoice_edit"
    const val ELECTRONIC_INVOICE_OTP = "electronic_invoice_otp"
    const val ELECTRONIC_INVOICE_SUCCESS = "electronic_invoice_success"
}

@Composable
fun IberdrolaNavHost(navController: NavHostController) {

    var isCloudEnabled by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToInvoices = { navController.navigate("invoice_list/$isCloudEnabled") },
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = { isCloudEnabled = it },
                onNavigateToElectronicInvoice = { navController.navigate(Routes.ELECTRONIC_INVOICE_SELECTION) }
            )
        }

        composable(
            Routes.INVOICE_LIST,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType })
        ) { backStackEntry ->
            val invoiceListViewModel: InvoiceListViewModel = hiltViewModel(backStackEntry)
            InvoiceListScreen(
                viewModel = invoiceListViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToFilters = { navController.navigate(Routes.FILTER) }
            )
        }

        composable(Routes.FILTER) { backStackEntry->
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

        composable(Routes.ELECTRONIC_INVOICE_SELECTION) { backStackEntry ->
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(backStackEntry)

            ElectronicInvoiceSelectionScreen(
                viewModel = hiltViewModel(),
                onNavigate = { contrato ->
                    sharedViewModel.selectContract(contrato)

                    if (contrato.isEnabled) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_DETAIL)
                    } else {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_FORM)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.ELECTRONIC_INVOICE_DETAIL) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
            }
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)

            ElectronicInvoiceDetailInfoScreen(
                viewModel = sharedViewModel,
                electronicInvoice = sharedViewModel.state.selectedContract,
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) },
                onNavigateToSuccess = {navController.navigate(Routes.ELECTRONIC_INVOICE_SUCCESS)}
            )
        }

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