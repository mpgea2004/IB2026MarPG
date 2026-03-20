package com.iberdrola.practicas2026.MarPG.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val password: String
)