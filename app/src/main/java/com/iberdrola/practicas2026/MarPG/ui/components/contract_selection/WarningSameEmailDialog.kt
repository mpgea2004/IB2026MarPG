package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail.ElectronicInvoiceViewModel
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp

@Composable
fun WarningSameEmailDialog(
    viewModel: ElectronicInvoiceViewModel
) {
    AlertDialog(
        onDismissRequest = { viewModel.dismissSameEmailWarning() },
        containerColor = WhiteApp,
        title = { 
            Text(
                text = stringResource(R.string.warning_same_email_title), 
                fontWeight = FontWeight.Bold,
                color =GreenDarkIberdrola,
                fontFamily = IberPangeaFamily
            ) 
        },
        text = { 
            Text(text = stringResource(R.string.warning_same_email_message),
                color = Color.DarkGray,
                fontFamily = IberPangeaFamily
            )
        },
        confirmButton = {
            Button(
                onClick = { viewModel.dismissSameEmailWarning() },
                colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.warning_same_email_confirm),color = WhiteApp,fontFamily = IberPangeaFamily)
            }
        }
    )
}
