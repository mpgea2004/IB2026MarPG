package com.iberdrola.practicas2026.MarPG.ui.components.contract_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.domain.model.ContractType
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.theme.GreenIberdrola
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily
import com.iberdrola.practicas2026.MarPG.ui.theme.LightGreenIberdrola

@Composable
fun ContractCard(
    elecInvoice: ElectronicInvoice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContractIcon(type = elecInvoice.type)

            Spacer(modifier = Modifier.width(16.dp))

            ContractInfo(
                type = elecInvoice.type,
                isEnabled = elecInvoice.isEnabled,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Gray
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}
@Composable
fun ContractIcon(type: ContractType) {
    when (type) {
        ContractType.LUZ -> {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = GreenIberdrola,
                modifier = Modifier.size(32.dp)
            )
        }
        ContractType.GAS -> {
            Icon(
                painter = painterResource(id = R.drawable.ic_invoice_gas),
                contentDescription = null,
                tint = GreenIberdrola,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
@Composable
fun ContractInfo(
    type: ContractType,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val title = if (type == ContractType.LUZ) stringResource(R.string.contract_card_light) else stringResource(R.string.contract_card_gas)
    val statusText = if (isEnabled) stringResource(R.string.contract_card_active) else stringResource(R.string.contract_card_inactive)

    val (backgroundColor, contentColor) = if (isEnabled) {
        LightGreenIberdrola to GreenIberdrola
    } else {
        Color.LightGray to Color.DarkGray
    }

    Column(modifier = modifier) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            fontFamily = IberPangeaFamily
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = statusText,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IberPangeaFamily
            )
        }
    }
}