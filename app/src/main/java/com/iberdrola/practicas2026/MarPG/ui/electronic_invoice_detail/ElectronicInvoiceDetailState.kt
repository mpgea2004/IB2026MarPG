package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.annotation.StringRes
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState

data class ElectronicInvoiceState(
    val selectedContract: ElectronicInvoice? = null,
    val emailInput: String = "",
    val otpInput: String = "",
    val isLegalAccepted: Boolean = false,

    val isLoading: Boolean = false,
    val isDeactivation: Boolean = false,
    val isSuccess: Boolean = false,
    @StringRes val error: Int? = null,

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
    val phoneError: String? = null,
    
    val showSameEmailWarning: Boolean = false,

    val showNoAddressDialog: Boolean = false,
    val newAddressInput: String = "",
    val showDeactivationConfirmDialog: Boolean = false
)
enum class ElectronicInvoiceStep {
    SELECTION, FORM, VERIFICATION, SUCCESS
}
