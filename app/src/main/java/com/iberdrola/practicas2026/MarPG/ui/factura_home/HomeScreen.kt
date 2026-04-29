package com.iberdrola.practicas2026.MarPG.ui.factura_home


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToInvoices: () -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFaq: () -> Unit,
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val currentUserName = viewModel.userName
    val isProfileComplete = viewModel.isProfileComplete
    val isFullProfileComplete = viewModel.isFullProfileComplete
    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isNavigating = false
    }

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
        isFullProfileComplete = isFullProfileComplete,
        onNavigateToInvoices = {
            if (!isNavigating) {
                if (isProfileComplete) {
                    isNavigating = true
                    onNavigateToInvoices()
                } else {
                    Toast.makeText(context, "Completa tu Nombre, Email y Password en el perfil para acceder", Toast.LENGTH_LONG).show()
                }
            }
        },
        onNavigateToElectronicInvoice = {
            if (!isNavigating) {
                if (isProfileComplete) {
                    isNavigating = true
                    onNavigateToElectronicInvoice()
                } else {
                    Toast.makeText(context, "Completa tu perfil para gestionar la factura electrónica", Toast.LENGTH_LONG).show()
                }
            }
        },
        onNavigateToProfile = {
            if (!isNavigating) {
                isNavigating = true
                onNavigateToProfile()
            }
        },
        onNavigateToFaq = {
            if (!isNavigating) {
                isNavigating = true
                onNavigateToFaq()
            }
        },
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    isCloudEnabled: Boolean,
    isSheetVisible: Boolean,
    sheetState: SheetState,
    isProfileComplete: Boolean,
    isFullProfileComplete: Boolean,
    onNavigateToInvoices: () -> Unit,
    onNavigateToElectronicInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFaq: () -> Unit,
    onToggleCloud: (Boolean) -> Unit,
    onSheetDismiss: () -> Unit,
    onSheetOptionSelected: (Int) -> Unit,
    currentUserName:String
) {
    Scaffold(
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFFF7F9F8),
                            0.6f to Color(0xFFF7F9F8),
                            1.0f to Color.White
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimateHomeItem(index = 0) {
                    HomeHeader(
                        userName = currentUserName,
                        isProfileComplete = isProfileComplete,
                        isFullProfileComplete = isFullProfileComplete,
                        onProfileClick = onNavigateToProfile
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                AnimateHomeItem(index = 1) {
                    Column(modifier = Modifier.alpha(if (isProfileComplete) 1f else 0.6f)) {
                        InvoiceNavigationCard(onClick = onNavigateToInvoices)
                        Spacer(modifier = Modifier.height(24.dp))
                        ElectronicInvoiceCard(onClick = onNavigateToElectronicInvoice)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(32.dp))

                AnimateHomeItem(index = 2) {
                    DataSourceConfigSection(
                        isCloudEnabled = isCloudEnabled,
                        onToggleCloud = onToggleCloud
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimateHomeItem(index = 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onNavigateToFaq() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = null,
                            tint = GreenIberdrola,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¿Necesitas ayuda? Consulta las FAQ",
                            color = GreenIberdrola,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = IberPangeaFamily,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
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

@Composable
fun AnimateHomeItem(
    index: Int,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 100))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 100)),
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeHeader(
    userName: String,
    isProfileComplete: Boolean,
    isFullProfileComplete: Boolean,
    onProfileClick: () -> Unit
) {

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

    val profileTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val infiniteTransition = rememberInfiniteTransition(label = "profilePulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderAlpha"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isFullProfileComplete) 360f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "profileRotation"
    )

    val targetBorderColor = when {
        isFullProfileComplete -> GreenIberdrola
        isProfileComplete -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 500),
        label = "borderColor"
    )

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

        TooltipBox(
            positionProvider = rememberPlainTooltipPositionProvider(),
            tooltip = {
                Surface(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when {
                            isFullProfileComplete -> "Perfil completo"
                            isProfileComplete -> "Faltan datos opcionales"
                            else -> "Perfil incompleto"
                        },
                        color = Color.White,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 12.sp
                    )
                }
            },
            state = profileTooltipState
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .rotate(rotation)
                    .border(
                        width = 2.dp,
                        color = if (isFullProfileComplete) borderColor else borderColor.copy(alpha = borderAlpha),
                        shape = CircleShape
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(GreenIberdrola.copy(alpha = 0.1f))
                    .combinedClickable(
                        onClick = onProfileClick,
                        onLongClick = {
                            haptic.performHapticFeedback(LongPress)
                            scope.launch { profileTooltipState.show() }
                        }
                    ),
                contentAlignment = Alignment.Center
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
}