package com.iberdrola.practicas2026.MarPG.ui.components

import com.iberdrola.practicas2026.MarPG.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp


@Composable
fun InvoiceEmptyState(
    message: String? = null,
    onRefresh: (() -> Unit)? = null
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message ?: stringResource(id = R.string.invoice_empty_state_message),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontFamily = IberPangeaFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.invoice_empty_state_subtitle),
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontFamily = IberPangeaFamily
        )

        if (onRefresh != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRefresh,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenIberdrola,
                    contentColor = WhiteApp
                )
            ) {
                Text(
                    text = stringResource(R.string.error_button_retry),
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}