package com.iberdrola.practicas2026.MarPG.ui.utils

object EmailUtils {
    /**
     * Transforma un email como "ejemplo@gmail.com" en "e******o@gmail.com"
     */
    fun obfuscateEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email // Si no es un email válido, lo devuelve tal cual

        val name = parts[0]
        val domain = parts[1]

        return when {
            name.length <= 2 -> "$name@$domain"
            else -> {
                val firstChar = name.first()
                val lastChar = name.last()
                val stars = "*".repeat(name.length - 2)
                "$firstChar$stars$lastChar@$domain"
            }
        }
    }
}