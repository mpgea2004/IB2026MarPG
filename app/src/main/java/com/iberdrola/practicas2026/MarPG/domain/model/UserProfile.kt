package com.iberdrola.practicas2026.MarPG.domain.model

data class UserProfile(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val password: String
)