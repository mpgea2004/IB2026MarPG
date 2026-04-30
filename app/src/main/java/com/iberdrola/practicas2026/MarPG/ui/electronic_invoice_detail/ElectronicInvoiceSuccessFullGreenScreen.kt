package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.remember
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
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.utils.EmailUtils

@Composable
fun ElectronicInvoiceSuccessFullGreenScreen(
    viewModel: ElectronicInvoiceViewModel,
    onFinish: () -> Unit
) {

    val state = viewModel.state

    BackHandler {
        onFinish()
    }

    val showEmail = EmailUtils.obfuscateEmail(state.emailInput)
    val isModification = state.isEditingEmail
    val isDeactivation = state.isDeactivation

    ElectronicInvoiceSuccessFullGreenContent(
        isModification = isModification,
        email = showEmail,
        onAccept = onFinish,
        isDeactivation = isDeactivation
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
        containerColor = GreenIberdrola,
        bottomBar = {
            AnimateSuccessItem(index = 3) {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    SuccessWhiteButton(
                        text = stringResource(R.string.success_button_accept), 
                        onClick = onAccept,
                        modifier = Modifier.padding(24.dp).padding(bottom = 8.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 16.dp), 
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.success_close_description),
                    tint = Color.White,
                    modifier = Modifier.size(32.dp).clickable { onAccept() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimateSuccessItem(index = 0) {
                SuccessIcon(iconResId)
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimateSuccessItem(index = 1) {
                SuccessText(title = title, subtitle = subTitle)
            }

            Spacer(modifier = Modifier.weight(1.2f))
        }
    }
}

@Composable
fun AnimateSuccessItem(
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
            animationSpec = tween(durationMillis = 800, delayMillis = (index * 150))
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 800, delayMillis = (index * 150)),
            initialOffsetY = { it / 2 }
        )
    ) {
        content()
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
            fontSize = 18.sp,
            fontFamily = IberPangeaFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            fontSize = 12.sp,
            fontFamily = IberPangeaFamily
        )
    }
}

@Composable
fun SuccessWhiteButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = GreenDarkIberdrola
        )
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = IberPangeaFamily)
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