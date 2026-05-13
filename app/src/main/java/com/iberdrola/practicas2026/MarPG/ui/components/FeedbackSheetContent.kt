package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily


@Composable
fun FeedbackSheetContent(
    isSubmitted: Boolean,
    onRatingClick: () -> Unit,
    onLaterClick: () -> Unit,
    onDontAskAgainClick: () -> Unit
) {
    AnimatedContent(
        targetState = isSubmitted,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "feedback_transition"
    ) { submitted ->
        if (submitted) {
            FeedbackSuccessState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.feedback_sheet_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = IberPangeaFamily,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.feedback_sheet_subtitle),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontFamily = IberPangeaFamily
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    val emojis = listOf(
                        Icons.Outlined.SentimentVeryDissatisfied to Color.Red,
                        Icons.Outlined.SentimentDissatisfied to Color(0xFFFF9800),
                        Icons.Outlined.SentimentNeutral to Color.Gray,
                        Icons.Outlined.SentimentSatisfied to Color(0xFF2196F3),
                        Icons.Outlined.SentimentVerySatisfied to Color(0xFF008244)
                    )

                    emojis.forEach { (icon, color) ->
                        IconButton(
                            onClick = onRatingClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.feedback_sheet_later),
                    color = Color(0xFF008244),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(35.dp))
                        .clickable { onLaterClick() }
                        .padding(horizontal = 8.dp),
                    fontFamily = IberPangeaFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.feedback_sheet_dont_ask),
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(35.dp))
                        .clickable { onDontAskAgainClick() }
                        .padding(horizontal = 8.dp),
                    fontFamily = IberPangeaFamily
                )
            }
        }
    }
}

@Composable
private fun FeedbackSuccessState() {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = GreenIberdrola,
            modifier = Modifier
                .size(80.dp)
                .scale(scale.value)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.feedback_thanks_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = IberPangeaFamily,
            color = GreenIberdrola,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.feedback_thanks_subtitle),
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = IberPangeaFamily,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}
