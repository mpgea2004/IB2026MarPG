package com.iberdrola.practicas2026.MarPG.data.dto

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id") val id: String,
    @SerializedName("contractType") val contractType: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("issueDate") val issueDate: String,
    @SerializedName("status") val status: String
)