package com.iberdrola.practicas2026.MarPG.ui.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.BackgroundApp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun FaqScreen(
    onBack: () -> Unit,
    viewModel: FaqViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state

    val events = FaqEvents(
        onToggleExpand = { id -> viewModel.onToggleExpand(id) },
        onBack = onBack,
        onContactSupport = { viewModel.openContactSupport(context) }
    )

    FaqContent(state = state, events = events)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqContent(
    state: FaqState,
    events: FaqEvents
) {
    Scaffold(
        containerColor = LightGreenIberdrola,
        topBar = {
            Surface(color = Color.Transparent) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { events.onBack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            null,
                            tint = GreenIberdrola,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.invoice_list_back),
                            color = GreenIberdrola,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            fontFamily = IberPangeaFamily
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, BackgroundApp)
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnimateFaqItemEntrance(index = 0) {
                        Column {
                            Text(
                                text = stringResource(R.string.faq_title),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = IberPangeaFamily,
                                color = GreenIberdrola,
                                lineHeight = 1.em,
                            )
                            Text(
                                text = stringResource(R.string.faq_subtitle),
                                fontSize = 16.sp,
                                color = TextGrey,
                                fontWeight = FontWeight.Medium,
                                fontFamily = IberPangeaFamily,
                                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                            )
                        }
                    }
                }

                itemsIndexed(state.faqList) { index, item ->
                    AnimateFaqItemEntrance(index = index + 1) {
                        FaqExpandableCard(
                            item = item,
                            isExpanded = state.expandedItems.contains(item.id),
                            onToggle = { events.onToggleExpand(item.id) }
                        )
                    }
                }

                item {
                    AnimateFaqItemEntrance(index = state.faqList.size + 1) {
                        Column {
                            Spacer(modifier = Modifier.height(32.dp))
                            ContactCard(onClick = { events.onContactSupport() })
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimateFaqItemEntrance(
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
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(500))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 600, delayMillis = (index * 80).coerceAtMost(500)),
            initialOffsetY = { 40 }
        )
    ) {
        Box(modifier = Modifier.padding(bottom = 12.dp).padding( horizontal = 4.dp)) {
            content()
        }
    }
}

@Composable
fun FaqExpandableCard(
    item: FaqItem,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "rotate")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteApp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        ),
        onClick = onToggle
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(item.question),
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = IberPangeaFamily,
                    color = if (isExpanded) GreenIberdrola else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (isExpanded) GreenIberdrola else Color.Gray,
                    modifier = Modifier.rotate(rotation)
                )
            }
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(item.answer),
                        fontSize = 14.sp,
                        color = TextGrey,
                        fontFamily = IberPangeaFamily,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GreenIberdrola.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = GreenIberdrola.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.HelpOutline, null, tint = GreenIberdrola)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    stringResource(R.string.faq_contact_title),
                    fontWeight = FontWeight.Bold,
                    fontFamily = IberPangeaFamily,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    stringResource(R.string.faq_contact_subtitle),
                    color = GreenIberdrola,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = IberPangeaFamily,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}
