package com.iberdrola.practicas2026.MarPG.ui.utils

object EmailUtils {
    fun obfuscateEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email 

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

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return email.matches(emailRegex.toRegex())
    }
}