package com.iberdrola.practicas2026.MarPG.ui.components.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun DataSourceConfigSection(
    isCloudEnabled: Boolean,
    onToggleCloud: (Boolean) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isCloudEnabled) 360f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "iconRotation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isCloudEnabled) GreenIberdrola else Color.Gray,
        animationSpec = tween(durationMillis = 400),
        label = "iconColor"
    )

    val iconBgColor by animateColorAsState(
        targetValue = if (isCloudEnabled) GreenIberdrola.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 400),
        label = "iconBgColor"
    )

    Column {
        Text(
            text = stringResource(R.string.home_footer_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
            fontFamily = IberPangeaFamily
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteApp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = iconBgColor,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Sync,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotation)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isCloudEnabled) stringResource(R.string.home_footer_typeS) else stringResource(R.string.home_footer_typeL),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = IberPangeaFamily
                    )
                    Text(
                        text = stringResource(R.string.home_footer_subtitle),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontFamily = IberPangeaFamily
                    )
                }
                
                Switch(
                    checked = isCloudEnabled,
                    onCheckedChange = onToggleCloud,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GreenIberdrola,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}
