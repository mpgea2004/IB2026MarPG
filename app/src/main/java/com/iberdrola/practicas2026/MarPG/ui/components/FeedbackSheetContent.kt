package com.iberdrola.practicas2026.MarPG.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R
import com.iberdrola.practicas2026.MarPG.ui.theme.IberPangeaFamily


/**
 * Contenido visual para el BottomSheet de Feedback
 * Presenta una escala de satisfacción mediante emojis y una opción para posponer
 * * @param onRatingClick Acción a ejecutar cuando el usuario pulsa cualquier emoji
 * @param onLaterClick Acción a ejecutar cuando el usuario pulsa "Responder más tarde"
 */
@Composable
fun FeedbackSheetContent(
    onRatingClick: () -> Unit,
    onLaterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.feedback_sheet_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = IberPangeaFamily)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.feedback_sheet_subtitle),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontFamily = IberPangeaFamily
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 0.5.dp, color = Color.LightGray)

        //Fila de Emojis con los colores de la imagen
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //el to color, hace que me pueda ahorrar el declarar 5 boton de iconos, asi lo hago con un foreach
            val emojis = listOf(
                Icons.Outlined.SentimentVeryDissatisfied to Color.Red,
                Icons.Outlined.SentimentDissatisfied to Color(0xFFFF9800), //Naranja
                Icons.Outlined.SentimentNeutral to Color.Gray,
                Icons.Outlined.SentimentSatisfied to Color(0xFF2196F3),   //Azul
                Icons.Outlined.SentimentVerySatisfied to Color(0xFF008244) //Verde
            )

            emojis.forEach { (icon, color) ->
                IconButton(onClick = onRatingClick) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        //Texto "Responder más tarde"
        Text(
            text = stringResource(R.string.feedback_sheet_later),
            color = Color(0xFF008244),
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onLaterClick() },
            fontFamily = IberPangeaFamily
        )
    }
}