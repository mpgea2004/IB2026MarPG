package com.iberdrola.practicas2026.MarPG.permissions

import android.os.Build

data class AppRuntimePermission(
    val permission: String,
    val minSdk: Int = Build.VERSION_CODES.M
) {
    fun appliesToDevice(): Boolean = Build.VERSION.SDK_INT >= minSdk
}
