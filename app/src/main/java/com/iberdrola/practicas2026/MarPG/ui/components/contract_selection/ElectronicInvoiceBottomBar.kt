package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun ElectronicInvoiceBottomBar(
    onBack: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean,
    backText: String = stringResource(R.string.bottom_bar_back),
    nextText: String = stringResource(R.string.bottom_bar_next),
    showBanner: Boolean = false,
    onCloseBanner: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhiteApp)
            .navigationBarsPadding()
    ) {

        AnimatedVisibility(
            visible = showBanner,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ResendSuccessBanner(onClose = onCloseBanner)
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GreenDarkIberdrola),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDarkIberdrola)
            ) {
                Text(backText, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
            }

            Button(
                onClick = onNext,
                enabled = isNextEnabled,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDarkIberdrola,
                    disabledContainerColor = Color(0xFFF2F4F2),
                    disabledContentColor = Color.LightGray,
                    contentColor = WhiteApp
                )
            ) {
                Text(nextText, fontWeight = FontWeight.Bold,fontFamily = IberPangeaFamily)
            }
        }
        Spacer(modifier = Modifier.height(44.dp))

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
    }
}
