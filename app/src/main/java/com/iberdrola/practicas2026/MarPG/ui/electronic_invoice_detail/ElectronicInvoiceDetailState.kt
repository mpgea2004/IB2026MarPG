package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState

data class ElectronicInvoiceState(
    val selectedContract: ElectronicInvoice? = null,
    val emailInput: String = "",
    val otpInput: String = "",
    val isLegalAccepted: Boolean = false,

    val isNextEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val isDeactivation: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val currentStep: ElectronicInvoiceStep = ElectronicInvoiceStep.SELECTION,

    val showResendSuccess: Boolean = false,

    val isEditingEmail: Boolean = false,

    val selectedLegalTitle: String? = null,
    val selectedLegalContent: String? = null,
    val showLegalSheet: Boolean = false,

    val resendAttempts: Int = 2,

    val userProfile: ProfileState = ProfileState(),

    val showNoPhoneDialog: Boolean = false,
    val newPhoneInput: String = "",
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val phoneError: String? = null
)
enum class ElectronicInvoiceStep {
    SELECTION, FORM, VERIFICATION, SUCCESS
}