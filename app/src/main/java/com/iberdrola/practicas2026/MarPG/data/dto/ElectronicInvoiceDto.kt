package com.iberdrola.practicas2026.MarPG.data.dto

import com.google.gson.annotations.SerializedName

data class ElectronicInvoiceDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("isEnabled") val isEnabled: Boolean,
    @SerializedName("email") val email: String
)