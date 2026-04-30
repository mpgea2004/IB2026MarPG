package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily

@Composable
fun FilterEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 60.dp,
                start = 32.dp,
                end = 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.filter_no_results),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            fontFamily = IberPangeaFamily
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}