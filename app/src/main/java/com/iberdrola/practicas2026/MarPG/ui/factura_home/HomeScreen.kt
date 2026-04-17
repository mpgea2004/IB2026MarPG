package com.iberdrola.practicas2026.MarPG.ui.factura_home


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.components.home.DataSourceConfigSection
import com.iberdrola.practicas2026.MarPG.ui.components.home.ElectronicInvoiceCard
import com.iberdrola.practicas2026.MarPG.ui.components.home.FeedbackBottomSheet
import com.iberdrola.practicas2026.MarPG.ui.components.home.InvoiceNavigationCard
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey

/** Pantalla principal: acceso a facturas y configuración de origen de datos */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInvoices: () -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    /** Estado que controla la animación y visibilidad del ModalBottomSheet */
    val sheetState = rememberModalBottomSheetState()

    val context = LocalContext.current

    val currentUserName = viewModel.userName

    val isProfileComplete = viewModel.isProfileComplete

    LaunchedEffect(viewModel.isSheetVisible) {
        if (!viewModel.isSheetVisible) {
            sheetState.hide()
        }
    }

    HomeContent(
        isCloudEnabled = isCloudEnabled,
        isSheetVisible = viewModel.isSheetVisible,
        sheetState = sheetState,
        isProfileComplete = isProfileComplete,
        onNavigateToInvoices = {
            if (isProfileComplete) {
                onNavigateToInvoices()
            } else {
                Toast.makeText(context, "Completa tu Nombre, Email y Password en el perfil para acceder", Toast.LENGTH_LONG).show()
            }
        },
        onNavigateToElectronicInvoice = {
            if (isProfileComplete) {
                onNavigateToElectronicInvoice()
            } else {
                Toast.makeText(context, "Completa tu perfil para gestionar la factura electrónica", Toast.LENGTH_LONG).show()
            }
        },
        onNavigateToProfile = onNavigateToProfile,
        onToggleCloud = onToggleCloud,
        onSheetDismiss = { viewModel.onOptionSelected(1) },
        onSheetOptionSelected = { tregua -> viewModel.onOptionSelected(tregua)
            if (tregua == 10) {
                Toast.makeText(
                    context,
                    context.getString(R.string.feedback_thanks_title),
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        currentUserName = currentUserName
    )
}

/** * Contenedor visual de la Home.
 * Separa la lógica de Scaffold y la estructura de la pantalla para facilitar Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isCloudEnabled: Boolean,
    isSheetVisible: Boolean,
    sheetState: SheetState,
    isProfileComplete: Boolean,
    onNavigateToInvoices: () -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onToggleCloud: (Boolean) -> Unit,
    onSheetDismiss: () -> Unit,
    onSheetOptionSelected: (Int) -> Unit,
    currentUserName:String
) {
    Scaffold(
        containerColor = Color(0xFFF7F9F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            HomeHeader(userName = currentUserName,
                onProfileClick = onNavigateToProfile)

            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.alpha(if (isProfileComplete) 1f else 0.6f)) {
                InvoiceNavigationCard(onClick = onNavigateToInvoices)
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(modifier = Modifier.alpha(if (isProfileComplete) 1f else 0.6f)) {
                ElectronicInvoiceCard(onClick = onNavigateToElectronicInvoice)
            }

            Spacer(modifier = Modifier.weight(1f))

            DataSourceConfigSection(
                isCloudEnabled = isCloudEnabled,
                onToggleCloud = onToggleCloud
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        if (isSheetVisible) {
            FeedbackBottomSheet(
                sheetState = sheetState,
                onDismiss = onSheetDismiss,
                onOptionSelected = onSheetOptionSelected
            )
        }
    }
}

/** Título y subtítulo de bienvenida */
@Composable
private fun HomeHeader(userName: String,onProfileClick: () -> Unit) {

    val processedName = remember(userName) {
        if (userName.isNotEmpty() && userName != "Usuario") {
            val capitalized = userName.split(" ")
                .filter { it.isNotEmpty() }
                .joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar { it.uppercase() }
                }
            if (capitalized.length > 15) {
                capitalized.take(15) + "..."
            } else {
                capitalized
            }
        } else {
            ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (userName.isNotEmpty() && userName != "Usuario") {
                    stringResource(R.string.home_header_welcome, processedName)
                } else {
                    stringResource(R.string.home_header_title)
                },
                fontSize = 32.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenIberdrola,
                fontFamily = IberPangeaFamily
            )
            Text(
                text = stringResource(R.string.home_header_subtitle),
                fontSize = 16.sp,
                color = TextGrey,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp),
                fontFamily = IberPangeaFamily
            )
        }

        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = GreenIberdrola.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.home_profile_description),
                tint = GreenIberdrola,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}