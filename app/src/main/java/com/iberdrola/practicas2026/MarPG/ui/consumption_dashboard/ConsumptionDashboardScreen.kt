package com.iberdrola.practicas2026.MarPG.ui.consumption_dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.GasMeter
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.*
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape.Companion.rounded

@Composable
fun ConsumptionDashboardScreen(
    viewModel: ConsumptionDashboardViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.state
    
    var isNavigating by remember { mutableStateOf(false) }

    val handleBack = {
        if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }

    BackHandler {
        handleBack()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            FilterTopBar(onBack = { handleBack() })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            GreenIberdrola.copy(alpha = 0.08f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                AnimateConsumptionItem(index = 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.consumption_title),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = IberPangeaFamily,
                                color = GreenIberdrola
                            )
                            Text(
                                text = stringResource(R.string.consumption_subtitle),
                                fontSize = 16.sp,
                                color = TextGrey,
                                fontWeight = FontWeight.Medium,
                                fontFamily = IberPangeaFamily
                            )
                        }
                        
                        Surface(
                            color = GreenIberdrola.copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (state.selectedType == ContractType.LUZ) Icons.Outlined.ElectricBolt else Icons.Outlined.GasMeter,
                                    contentDescription = null,
                                    tint = GreenIberdrola,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimateConsumptionItem(index = 1) {
                    SuministroToggle(
                        selectedType = state.selectedType,
                        onTypeSelected = { viewModel.onTypeSelected(it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedContent(
                    targetState = state.isLoading,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "loading_content"
                ) { isLoading ->
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = GreenIberdrola,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    } else if (state.chartData.isEmpty()) {
                        EmptyConsumptionState()
                    } else {
                        Column {
                            AnimateConsumptionItem(index = 2) {
                                ConsumptionChart(state.chartData)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            AnimateConsumptionItem(index = 3) {
                                ComparisonCard(
                                    message = state.comparisonMessage,
                                    isPositive = state.isPositiveTrend
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            AnimateConsumptionItem(index = 4) {
                                SavingsTipCard()
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AnimateConsumptionItem(
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
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

@Composable
fun SuministroToggle(
    selectedType: ContractType,
    onTypeSelected: (ContractType) -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp)
                .height(48.dp)
        ) {
            val modifier = Modifier.weight(1f).fillMaxHeight()
            
            ToggleButton(
                text = stringResource(R.string.consumption_tab_electricity),
                isSelected = selectedType == ContractType.LUZ,
                onClick = { onTypeSelected(ContractType.LUZ) },
                modifier = modifier
            )
            ToggleButton(
                text = stringResource(R.string.consumption_tab_gas),
                isSelected = selectedType == ContractType.GAS,
                onClick = { onTypeSelected(ContractType.GAS) },
                modifier = modifier
            )
        }
    }
}

@Composable
fun ToggleButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        onClick = onClick,
        color = if (isSelected) GreenIberdrola else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = IberPangeaFamily,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ConsumptionChart(data: List<Pair<String, Double>>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data.map { it.second })
            }
        }
    }

    val columnLine = rememberLineComponent(
        color = GreenIberdrola,
        thickness = 16.dp,
        shape = rounded(topLeftPercent = 40, topRightPercent = 40)
    )
    
    val axisLabel = rememberTextComponent(
        color = Color.Gray.copy(alpha = 0.8f),
        textSize = 10.sp
    )
    
    val guidelineLine = rememberLineComponent(
        color = Color.LightGray.copy(alpha = 0.2f),
        thickness = 1.dp
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, GreenIberdrola.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, GreenIberdrola.copy(alpha = 0.02f))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.consumption_chart_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = IberPangeaFamily,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "€",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenIberdrola,
                    fontFamily = IberPangeaFamily
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CartesianChartHost(
                chart = rememberCartesianChart(
                    layers = arrayOf(
                        rememberColumnCartesianLayer(
                            columnProvider = ColumnCartesianLayer.ColumnProvider.series(columnLine)
                        )
                    ),
                    startAxis = rememberStartAxis(
                        label = axisLabel,
                        tickLength = 0.dp,
                        guideline = guidelineLine
                    ),
                    bottomAxis = rememberBottomAxis(
                        label = axisLabel,
                        tickLength = 0.dp,
                        guideline = null,
                        valueFormatter = { value, _, _ ->
                            data.getOrNull(value.toInt())?.first ?: ""
                        }
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier.height(220.dp),
                scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End)
            )
        }
    }
}

@Composable
fun ComparisonCard(message: String, isPositive: Boolean) {
    val color = if (isPositive) GreenIberdrola else Color(0xFFE53935)
    val icon = if (isPositive) Icons.AutoMirrored.Outlined.TrendingDown else Icons.AutoMirrored.Outlined.TrendingUp
    val bgColor = if (isPositive) GreenIberdrola.copy(alpha = 0.08f) else Color(0xFFE53935).copy(alpha = 0.08f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = IberPangeaFamily,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun SavingsTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color(0xFF0288D1),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.consumption_tip_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = IberPangeaFamily
                )
                Text(
                    text = stringResource(R.string.consumption_tip_message),
                    fontSize = 13.sp,
                    color = TextGrey,
                    fontFamily = IberPangeaFamily
                )
            }
        }
    }
}

@Composable
fun EmptyConsumptionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.consumption_empty_title),
            fontFamily = IberPangeaFamily,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.consumption_empty_message),
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}