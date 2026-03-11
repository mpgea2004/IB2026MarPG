package com.iberdrola.practicas2026.MarPG.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val contractType: String, // Guardo "LUZ" o "GAS"
    val amount: Double,
    val startDate: String,
    val endDate: String,
    val issueDate: String,
    val status: String        // Guardo "PAID" o "PENDING"
)