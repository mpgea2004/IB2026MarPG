package com.iberdrola.practicas2026.MarPG.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.iberdrola.practicas2026.MarPG.R

val IberPangeaFamily = FontFamily(
    Font(R.font.iber_pangea, FontWeight.Normal),
    Font(R.font.pangea_bold, FontWeight.ExtraBold),
    Font(R.font.pangea_medium, FontWeight.Bold)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = IberPangeaFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = IberPangeaFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = IberPangeaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = IberPangeaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    )

)