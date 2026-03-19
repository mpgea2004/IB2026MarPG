package com.iberdrola.practicas2026.MarPG.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electronic_invoices")
data class ElectronicInvoiceEntity(
    @PrimaryKey val id: String,
    val type: String,
    val isEnabled: Boolean,
    val email: String?
)