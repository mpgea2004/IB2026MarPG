package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola

@Composable
fun WarningSameEmailDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.warning_same_email_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(text = stringResource(R.string.warning_same_email_message))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDarkIberdrola)
            ) {
                Text(text = stringResource(R.string.warning_same_email_confirm))
            }
        }
    )
}
