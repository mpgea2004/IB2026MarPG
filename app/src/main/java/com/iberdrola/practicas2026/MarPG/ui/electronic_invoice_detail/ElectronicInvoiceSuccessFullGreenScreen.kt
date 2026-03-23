package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils

@Composable
fun ElectronicInvoiceSuccessFullGreenScreen(
    viewModel: ElectronicInvoiceViewModel,
    onFinish: () -> Unit
) {

    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.logSuccessScreen(
            isDeactivation = state.isDeactivation,
            isEditingEmail = state.isEditingEmail,
            contractType = state.selectedContract?.type?.name ?: "unknown"
        )
    }


    BackHandler {
        onFinish()
    }

    val showEmail = EmailUtils.obfuscateEmail(state.emailInput)


    ElectronicInvoiceSuccessFullGreenContent(
        isModification = state.isEditingEmail,
        isDeactivation = state.isDeactivation,
        email = showEmail,
        onAccept = onFinish
    )
}

@Composable
fun ElectronicInvoiceSuccessFullGreenContent(
    isModification: Boolean,
    isDeactivation: Boolean,
    email: String,
    onAccept: () -> Unit
) {
    val title = when {
        isDeactivation -> stringResource(R.string.success_deactivation_title)
        isModification -> stringResource(R.string.success_modification_title)
        else -> stringResource(R.string.success_activation_title)
    }

    val subTitle = if (isDeactivation) {
        stringResource(R.string.success_deactivation_subtitle)
    } else {
        stringResource(R.string.success_activation_subtitle, email)
    }

    val iconResId = if (isDeactivation) {
        R.drawable.ic_invoice_off
    } else {
        R.drawable.ic_success_thumbs_up
    }

    Scaffold(
        containerColor = GreenDarkIberdrola,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.success_close_description),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp).clickable { onAccept() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            SuccessIcon(iconResId)

            Spacer(modifier = Modifier.height(40.dp))

            SuccessText(title = title, subtitle = subTitle)

            Spacer(modifier = Modifier.weight(1.2f))

            SuccessWhiteButton(text = stringResource(R.string.success_button_accept), onClick = onAccept)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SuccessIcon(iconResId: Int) {

    Icon(
        painter = painterResource(iconResId),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(160.dp)
    )
}

@Composable
fun SuccessText(title: String,subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SuccessWhiteButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = GreenDarkIberdrola
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}


@Preview(name = "Caso Activación")
@Composable
fun PreviewSuccessActivation() {
    MaterialTheme {
        ElectronicInvoiceSuccessFullGreenContent(
            isModification = false,
            email = "m*******a@gmail.com",
            onAccept = {},
            isDeactivation = false
        )
    }
}

@Preview(name = "Caso Modificación")
@Composable
fun PreviewSuccessModification() {
    MaterialTheme {
        ElectronicInvoiceSuccessFullGreenContent(
            isModification = true,
            email = "b********5@imfaya.com",
            onAccept = {},
            isDeactivation = false
        )
    }
}

@Preview(name = "Caso Desactivacion")
@Composable
fun PreviewSuccessDeactivation() {
    MaterialTheme {
        ElectronicInvoiceSuccessFullGreenContent(
            isModification = false,
            email = "b********5@imfaya.com",
            onAccept = {},
            isDeactivation = true
        )
    }
}