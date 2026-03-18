package com.iberdrola.practicas2026.MarPG.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contracts")
data class ElectronicInvoiceEntity(
    @PrimaryKey val id: String,
    val type: String, // "LUZ" o "GAS"
    val isEnabled: Boolean,
    val email: String?
)