package com.iberdrola.practicas2026.MarPG.permissions

import android.Manifest
import android.os.Build

object AppPermissions {

    val Notifications = AppRuntimePermission(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        minSdk = Build.VERSION_CODES.TIRAMISU
    )
}
