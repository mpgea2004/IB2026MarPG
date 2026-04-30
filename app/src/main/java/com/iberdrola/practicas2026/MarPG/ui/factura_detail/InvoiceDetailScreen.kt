package com.iberdrola.practicas2026.MarPG.ui.factura_detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberPlainTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.InvoiceStatus
import com.iberdrola.practicas2026.MarPG.domain.utils.DateMapper
import com.iberdrola.practicas2026.MarPG.ui.components.detail.InvoiceStepper
import com.iberdrola.practicas2026.MarPG.ui.components.detail.ShimmerInvoiceDetail
import com.iberdrola.practicas2026.MarPG.ui.components.list.StatusBadge
import com.iberdrola.practicas2026.MarPG.ui.components.shimmerBrush
import com.iberdrola.practicas2026.MarPG.ui.factura_filter.FilterTopBar
import com.iberdrola.practicas2026.MarPG.ui.theme.BackgroundApp
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenDarkIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.TextGrey
import com.iberdrola.practicas2026.MarPG.ui.theme.WhiteApp
import com.iberdrola.practicas2026.MarPG.ui.utils.InvoicePdfGenerator
import com.iberdrola.practicas2026.MarPG.ui.utils.getHiddenAmountAnnotatedString
import com.iberdrola.practicas2026.MarPG.ui.utils.toAnnotatedCurrencyFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InvoiceDetailScreen(
    viewModel: InvoiceDetailViewModel,
    isCloudEnabled: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.state
    val haptic = LocalHapticFeedback.current

    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val handleBack = {
        if (!isNavigating) {
            isNavigating = true
            onBack()
        }
    }
    BackHandler(enabled = true) { handleBack() }

    val events = InvoiceDetailEvents(
        onBack = handleBack,
        onDownloadPdf = { viewModel.downloadPdf(context) },
        onPayClick = { viewModel.onPayClick() },
        onPayConfirm = { viewModel.confirmPayment(isCloudEnabled) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onDismissPasswordDialog = { viewModel.dismissPasswordDialog() },
        onDismissOverdueDialog = { viewModel.dismissOverdueDialog() },
        onDismissPdf = { viewModel.dismissPdfViewer() },
        onToggleAmountVisibility = { viewModel.toggleAmountVisibility() }
    )

    InvoiceDetailContent(
        state = state,
        events = events
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InvoiceDetailContent(
    state: InvoiceDetailState,
    events: InvoiceDetailEvents
) {
    val invoice = state.invoice
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val downloadTooltipState = rememberTooltipState(isPersistent = false)
    val visibilityTooltipState = rememberTooltipState(isPersistent = false)
    val copyTooltipState = rememberTooltipState(isPersistent = false)
    val scope = rememberCoroutineScope()

    if (state.showPayPasswordDialog) {
        PayPasswordDialog(
            passwordInput = state.payPasswordInput,
            isError = state.payPasswordError,
            onPasswordChange = events.onPasswordChange,
            onConfirm = events.onPayConfirm,
            onDismiss = events.onDismissPasswordDialog
        )
    }

    if (state.showOverdueDialog) {
        OverdueInvoiceDialog(
            onDismiss = events.onDismissOverdueDialog
        )
    }

    if (state.showPdfViewer && state.pdfUri != null) {
        PdfViewerDialog(
            uri = state.pdfUri,
            onDismiss = events.onDismissPdf
        )
    }

    Scaffold(
        topBar = {
            FilterTopBar(onBack = events.onBack)
        },

        bottomBar = {
            if(invoice!= null) {
                val isPayable = invoice.status != InvoiceStatus.PAGADAS && 
                               invoice.status != InvoiceStatus.ANULADAS && 
                               invoice.status != InvoiceStatus.CUOTA_FIJA

                if (isPayable) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                        color = Color.Transparent,
                        shadowElevation = 16.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = if (state.isOverdue) 
                                            listOf(Color(0xFFFFF3E0), Color.White) 
                                        else 
                                            listOf(Color.White, Color.White)
                                    )
                                )
                                .navigationBarsPadding()
                                .padding(bottom = 8.dp)
                        ) {
                            if (state.isOverdue) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(R.string.invoice_detail_overdue_short),
                                            color = Color(0xFFE65100),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = IberPangeaFamily
                                        )
                                    }
                                }
                            }
                            
                            Button(
                                onClick = events.onPayClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 20.dp)
                                    .height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isOverdue) Color.LightGray else GreenIberdrola,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray,
                                    disabledContentColor = Color.White
                                ),
                                enabled = !state.isLoading && !state.isOverdue
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = if (state.isOverdue) stringResource(R.string.invoice_detail_pay_not_available) else stringResource(R.string.invoice_detail_pay_now),
                                        fontFamily = IberPangeaFamily,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = WhiteApp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = BackgroundApp
    ) { padding ->
        if (invoice == null) {
            Box(Modifier
                .fillMaxSize()
                .padding(padding)) {
                ShimmerInvoiceDetail(brush = shimmerBrush())
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhiteApp)
                            .padding(horizontal = 20.dp)
                            .height(56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.invoice_detail_title),
                            fontFamily = IberPangeaFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TooltipBox(
                                positionProvider = rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    Surface(
                                        color = Color.DarkGray,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (state.isAmountVisible) stringResource(R.string.invoice_list_hide_amounts) else stringResource(R.string.invoice_list_show_amounts),
                                            color = Color.White,
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                },
                                state = visibilityTooltipState
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .combinedClickable(
                                            onClick = events.onToggleAmountVisibility,
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                scope.launch { visibilityTooltipState.show() }
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (state.isAmountVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(R.string.invoice_list_change_visibility),
                                        tint = GreenIberdrola
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier.size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isDownloadingPdf) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = GreenIberdrola,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    TooltipBox(
                                        positionProvider = rememberPlainTooltipPositionProvider(),
                                        tooltip = {
                                            Surface(
                                                color = Color.DarkGray,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.invoice_detail_download_pdf),
                                                    color = Color.White,
                                                    modifier = Modifier.padding(8.dp),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        },
                                        state = downloadTooltipState
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .combinedClickable(
                                                    onClick = { events.onDownloadPdf() },
                                                    onLongClick = {
                                                        scope.launch { downloadTooltipState.show() }
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.CloudDownload,
                                                contentDescription = stringResource(R.string.invoice_detail_download_pdf),
                                                tint = GreenIberdrola
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    AnimateDetailItemEntrance(index = 0) {
                        InvoiceDetailHeader(
                            amount = invoice.amount,
                            date = DateMapper.formatToDisplay(invoice.issueDate),
                            isAmountVisible = state.isAmountVisible
                        )
                    }
                }

                item {
                    AnimateDetailItemEntrance(index = 1) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = WhiteApp
                        ) {
                            InvoiceStepper(
                                status = invoice.status,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.isOverdue && invoice.status != InvoiceStatus.PAGADAS) {
                            AnimateDetailItemEntrance(index = 2) {
                                OverdueWarningBanner()
                            }
                        }
                        
                        if (invoice.status == InvoiceStatus.CUOTA_FIJA) {
                            AnimateDetailItemEntrance(index = 3) {
                                FixedQuotaBanner()
                            }
                        }

                        if (state.paymentSuccess) {
                            StatusMessage(message = stringResource(R.string.invoice_detail_pay_success), color = GreenIberdrola)
                        }

                        if (state.paymentError) {
                            StatusMessage(message = stringResource(R.string.invoice_detail_pay_error), color = Color.Red)
                        }

                        if (state.pdfDownloaded) {
                            StatusMessage(message = stringResource(R.string.invoice_detail_download_success), color = GreenIberdrola)
                        }

                        AnimateDetailItemEntrance(index = 4) {
                            InfoCard(
                                title = stringResource(R.string.invoice_detail_section_supply),
                                icon = Icons.Outlined.Receipt
                            ) {
                                DetailRow(
                                    label = stringResource(R.string.invoice_detail_label_contract_type),
                                    value = if (invoice.contractType == ContractType.LUZ) stringResource(R.string.invoice_detail_value_light) else stringResource(R.string.invoice_detail_value_gas)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(R.string.invoice_detail_label_id), fontSize = 14.sp, color = TextGrey, fontFamily = IberPangeaFamily)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(invoice.id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), fontFamily = IberPangeaFamily)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        
                                        TooltipBox(
                                            positionProvider = rememberPlainTooltipPositionProvider(),
                                            tooltip = {
                                                Surface(
                                                    color = Color.DarkGray,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.invoice_detail_copy_id),
                                                        color = Color.White,
                                                        modifier = Modifier.padding(8.dp),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            },
                                            state = copyTooltipState
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .combinedClickable(
                                                        onClick = {
                                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            val clip = ClipData.newPlainText(context.getString(R.string.invoice_detail_clip_label), invoice.id)
                                                            clipboard.setPrimaryClip(clip)
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            Toast.makeText(context, context.getString(R.string.invoice_detail_copy_toast), Toast.LENGTH_SHORT).show()
                                                        },
                                                        onLongClick = {
                                                            scope.launch { copyTooltipState.show() }
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.ContentCopy,
                                                    contentDescription = stringResource(R.string.invoice_detail_copy_id),
                                                    tint = GreenIberdrola,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AnimateDetailItemEntrance(index = 5) {
                            InfoCard(
                                title = stringResource(R.string.invoice_detail_section_period),
                                icon = Icons.Outlined.Info
                            ) {
                                val range = "${DateMapper.formatToShortDisplay(invoice.startDate)} - ${DateMapper.formatToShortDisplay(invoice.endDate)}"
                                Text(
                                    text = range,
                                    fontFamily = IberPangeaFamily,
                                    fontSize = 15.sp,
                                    color = Color(0xFF333333),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        AnimateDetailItemEntrance(index = 6) {
                            InfoCard(
                                title = stringResource(R.string.invoice_detail_section_status),
                                icon = Icons.Outlined.Info
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (invoice.status == InvoiceStatus.PAGADAS || invoice.status == InvoiceStatus.CUOTA_FIJA) stringResource(R.string.invoice_detail_status_paid) else {
                                            if (invoice.status == InvoiceStatus.ANULADAS) stringResource(R.string.invoice_detail_status_cancelled) else stringResource(R.string.invoice_detail_status_pending)
                                        },
                                        fontFamily = IberPangeaFamily,
                                        fontSize = 14.sp,
                                        color = TextGrey
                                    )
                                    StatusBadge(invoice.status)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun AnimateDetailItemEntrance(
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
            initialOffsetY = { it / 4 }
        )
    ) {
        content()
    }
}

@Composable
fun PayPasswordDialog(
    passwordInput: String,
    isError: Boolean,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.invoice_detail_dialog_pay_title),
                fontFamily = IberPangeaFamily,
                fontWeight = FontWeight.Bold,
                color = GreenDarkIberdrola
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.invoice_detail_dialog_pay_text),
                    fontFamily = IberPangeaFamily,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.invoice_detail_label_password), fontFamily = IberPangeaFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = isError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, null, tint = TextGrey)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenIberdrola,
                        focusedLabelColor = GreenIberdrola,
                        cursorColor = GreenIberdrola
                    )
                )
                if (isError) {
                    Text(
                        text = stringResource(R.string.invoice_detail_error_password),
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontFamily = IberPangeaFamily,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GreenIberdrola)
            ) {
                Text(stringResource(R.string.invoice_detail_button_confirm_pay), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.invoice_detail_dialog_cancel), color = Color.Gray)
            }
        },
        containerColor = WhiteApp,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OverdueInvoiceDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(32.dp)) },
        title = {
            Text(
                text = stringResource(R.string.invoice_detail_dialog_overdue_title),
                fontFamily = IberPangeaFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFD32F2F)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.invoice_detail_dialog_overdue_text),
                fontFamily = IberPangeaFamily,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(stringResource(R.string.invoice_detail_button_understood), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = WhiteApp,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OverdueWarningBanner() {
    Surface(
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.invoice_detail_banner_overdue),
                color = Color(0xFFE65100),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
fun PdfViewerDialog(uri: android.net.Uri, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            try {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    val renderer = PdfRenderer(pfd)
                    val page = renderer.openPage(0)
                    val bitmap = createBitmap(page.width * 2, page.height * 2,
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    pdfBitmap = bitmap
                    page.close()
                    renderer.close()
                    pfd.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() }, 
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(WhiteApp)
                    .clickable(enabled = false) { } 
            ) {
                // Cabecera Premium
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GreenDarkIberdrola, GreenIberdrola)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.invoice_detail_preview_title),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontFamily = IberPangeaFamily
                            )
                            Text(
                                text = stringResource(R.string.invoice_detail_preview_subtitle),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = IberPangeaFamily
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { InvoicePdfGenerator.shareInvoicePdf(context, uri) },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = stringResource(R.string.invoice_detail_share),
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.success_close_description),
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF0F2F5)),
                    contentAlignment = Alignment.Center
                ) {
                    if (pdfBitmap != null) {
                        Card(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxSize(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
                            shape = RoundedCornerShape(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Image(
                                bitmap = pdfBitmap!!.asImageBitmap(),
                                contentDescription = stringResource(R.string.invoice_detail_preview_subtitle),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        CircularProgressIndicator(color = GreenIberdrola, strokeWidth = 3.dp)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.invoice_detail_download_location),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontFamily = IberPangeaFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun FixedQuotaBanner() {
    Surface(
        color = Color(0xFFE3F2FD),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Sync, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.invoice_detail_banner_fixed_quota),
                color = Color(0xFF1976D2),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
private fun StatusMessage(message: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Info, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = color,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}

@Composable
private fun InvoiceDetailHeader(amount: Double, date: String, isAmountVisible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhiteApp)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.invoice_detail_total_amount), fontFamily = IberPangeaFamily, fontSize = 14.sp, color = TextGrey)
            
            if (isAmountVisible) {
                Text(
                    text = amount.toAnnotatedCurrencyFormat(32.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = IberPangeaFamily
                )
            } else {
                Text(
                    text = getHiddenAmountAnnotatedString(32.sp),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = IberPangeaFamily
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = BackgroundApp, shape = RoundedCornerShape(16.dp)) {
                Text(
                    text = stringResource(R.string.invoice_detail_issued_date, date),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontFamily = IberPangeaFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = GreenIberdrola, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontFamily = IberPangeaFamily, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF333333))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextGrey, fontFamily = IberPangeaFamily)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), fontFamily = IberPangeaFamily)
    }
}