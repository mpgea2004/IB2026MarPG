package com.iberdrola.practicas2026.MarPG.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard.ConsumptionDashboardScreen
import com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard.ConsumptionDashboardViewModel
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceDetailFormScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceDetailInfoScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceEditEmailScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceOtpScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceSuccessFullGreenScreen
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceViewModel
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection.ElectronicInvoiceListViewModel
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_selection.ElectronicInvoiceSelectionScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_detail.InvoiceDetailScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_detail.InvoiceDetailViewModel
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_home.HomeScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListScreen
import com.iberdrola.practicas2026.MarPG.ui.factura_list.InvoiceListViewModel
import com.iberdrola.practicas2026.MarPG.ui.faq.FaqScreen
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileScreen

object Routes {
    const val USER_PROFILE = "user_profile"
    const val HOME = "home"
    const val INVOICE_LIST = "invoice_list/{isCloud}"
    const val FILTER = "filter"
    const val INVOICE_DETAIL = "invoice_detail/{invoiceId}"
    const val ELECTRONIC_INVOICE_SELECTION = "electronic_invoice_selection/{isCloud}"
    const val ELECTRONIC_INVOICE_DETAIL = "electronic_invoice_detail"
    const val ELECTRONIC_INVOICE_FORM = "electronic_invoice_form"
    const val ELECTRONIC_INVOICE_EDIT_EMAIL = "electronic_invoice_edit"
    const val ELECTRONIC_INVOICE_OTP = "electronic_invoice_otp"
    const val ELECTRONIC_INVOICE_SUCCESS = "electronic_invoice_success"
    const val CONSUMPTION_DASHBOARD = "consumption_dashboard/{isCloud}"
    const val FAQ = "faq"
}

@Composable
fun IberdrolaNavHost(navController: NavHostController) {
    var isCloudEnabled by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(
            route = Routes.HOME,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) {
            HomeScreen(
                onNavigateToInvoices = { 
                    if (navController.currentDestination?.route == Routes.HOME) {
                        navController.navigate("invoice_list/$isCloudEnabled") 
                    }
                },
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = { isCloudEnabled = it },
                onNavigateToElectronicInvoice = { 
                    if (navController.currentDestination?.route == Routes.HOME) {
                        navController.navigate("electronic_invoice_selection/$isCloudEnabled") 
                    }
                },
                onNavigateToProfile = { 
                    if (navController.currentDestination?.route == Routes.HOME) {
                        navController.navigate(Routes.USER_PROFILE) 
                    }
                },
                onNavigateToFaq = { 
                    if (navController.currentDestination?.route == Routes.HOME) {
                        navController.navigate(Routes.FAQ) 
                    }
                }
            )
        }

        composable(
            route = Routes.CONSUMPTION_DASHBOARD,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val isCloud = backStackEntry.arguments?.getBoolean("isCloud") ?: false
            val viewModel: ConsumptionDashboardViewModel = hiltViewModel()

            LaunchedEffect(isCloud) {
                viewModel.setCloudMode(isCloud)
            }

            ConsumptionDashboardScreen(
                viewModel = viewModel,
                onBack = {
                    if (navController.currentDestination?.route?.startsWith("consumption_dashboard") == true) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Routes.USER_PROFILE,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) {
            ProfileScreen(
                onBack = {
                    if (navController.currentDestination?.route == Routes.USER_PROFILE) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Routes.INVOICE_LIST,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val invoiceListViewModel: InvoiceListViewModel = hiltViewModel(backStackEntry)
            val isCloud = backStackEntry.arguments?.getBoolean("isCloud") ?: false

            InvoiceListScreen(
                viewModel = invoiceListViewModel,
                onBack = {
                    if (navController.currentDestination?.route == Routes.INVOICE_LIST) {
                        navController.popBackStack()
                    }
                },
                onNavigateToFilters = { 
                    if (navController.currentDestination?.route == Routes.INVOICE_LIST) {
                        navController.navigate(Routes.FILTER) 
                    }
                },
                onNavigateToInvoiceDetail = { invoice ->
                    if (navController.currentDestination?.route == Routes.INVOICE_LIST) {
                        navController.navigate("invoice_detail/${invoice.id}")
                    }
                },
                onNavigateToConsumption = {
                    if (navController.currentDestination?.route == Routes.INVOICE_LIST) {
                        navController.navigate("consumption_dashboard/$isCloud")
                    }
                }
            )
        }

        composable(
            route = Routes.FILTER,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry("invoice_list/{isCloud}")
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val invoiceListViewModel: InvoiceListViewModel = hiltViewModel(parentEntry)

            FilterScreen(
                listViewModel = invoiceListViewModel,
                filterViewModel = hiltViewModel(),
                onBack = {
                    if (navController.currentDestination?.route == Routes.FILTER) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Routes.INVOICE_DETAIL,
            arguments = listOf(navArgument("invoiceId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry("invoice_list/{isCloud}")
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val detailViewModel: InvoiceDetailViewModel = hiltViewModel(parentEntry)

            LaunchedEffect(invoiceId) {
                detailViewModel.loadInvoice(invoiceId)
            }

            InvoiceDetailScreen(
                viewModel = detailViewModel,
                isCloudEnabled = isCloudEnabled,
                onBack = {
                    if (navController.currentDestination?.route == Routes.INVOICE_DETAIL) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_SELECTION,
            arguments = listOf(navArgument("isCloud") { type = NavType.BoolType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val listViewModel: ElectronicInvoiceListViewModel = hiltViewModel()
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(backStackEntry)

            ElectronicInvoiceSelectionScreen(
                viewModel = listViewModel,
                onNavigate = { contrato ->
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_SELECTION) {
                        sharedViewModel.selectContract(contrato)
                        if (contrato.isEnabled) {
                            navController.navigate(Routes.ELECTRONIC_INVOICE_DETAIL)
                        } else {
                            navController.navigate(Routes.ELECTRONIC_INVOICE_FORM)
                        }
                    }
                },
                onBack = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_SELECTION) {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_DETAIL,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val sharedViewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)

            ElectronicInvoiceDetailInfoScreen(
                viewModel = sharedViewModel,
                electronicInvoice = sharedViewModel.state.selectedContract,
                onBack = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_DETAIL) {
                        navController.popBackStack()
                    }
                },
                onNavigateToEdit = { 
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_DETAIL) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) 
                    }
                },
                onNavigateToSuccess = { 
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_DETAIL) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_SUCCESS) 
                    }
                }
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_FORM,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)
            ElectronicInvoiceDetailFormScreen(
                viewModel = viewModel,
                onBack = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_FORM) {
                        navController.popBackStack()
                    }
                },
                onNext = { 
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_FORM) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_OTP) 
                    }
                },
                onCloseToHome = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_FORM) {
                        viewModel.discardChanges()
                        navController.popBackStack(Routes.ELECTRONIC_INVOICE_SELECTION, false)
                    }
                },
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_EDIT_EMAIL,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)
            ElectronicInvoiceEditEmailScreen(
                viewModel = viewModel,
                onBack = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) {
                        navController.popBackStack()
                    }
                },
                onNext = { 
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_OTP) 
                    }
                },
                onCloseToHome = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_EDIT_EMAIL) {
                        viewModel.discardChanges()
                        navController.popBackStack(Routes.ELECTRONIC_INVOICE_SELECTION, false)
                    }
                },
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_OTP,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val viewModel: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)
            ElectronicInvoiceOtpScreen(
                viewModel = viewModel,
                onBack = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_OTP) {
                        navController.popBackStack()
                    }
                },
                onNext = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_OTP) {
                        navController.navigate(Routes.ELECTRONIC_INVOICE_SUCCESS)
                    }
                },
                onCloseToHome = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_OTP) {
                        viewModel.discardChanges()
                        navController.popBackStack(Routes.ELECTRONIC_INVOICE_SELECTION, false)
                    }
                },
            )
        }

        composable(
            route = Routes.ELECTRONIC_INVOICE_SUCCESS,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry(Routes.ELECTRONIC_INVOICE_SELECTION)
                } catch (e: Exception) {
                    backStackEntry
                }
            }
            val vm: ElectronicInvoiceViewModel = hiltViewModel(parentEntry)

            ElectronicInvoiceSuccessFullGreenScreen(
                viewModel = vm,
                onFinish = {
                    if (navController.currentDestination?.route == Routes.ELECTRONIC_INVOICE_SUCCESS) {
                        navController.popBackStack(Routes.HOME, false)
                    }
                }
            )
        }

        composable(
            route = Routes.FAQ,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400)) }
        ) {
            FaqScreen(
                onBack = {
                    if (navController.currentDestination?.route == Routes.FAQ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
