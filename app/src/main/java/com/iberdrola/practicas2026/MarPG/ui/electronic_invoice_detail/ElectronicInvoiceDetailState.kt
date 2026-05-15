package com.iberdrola.practicas2026.MarPG.ui.electronic_invoice_detail

import androidx.annotation.StringRes
import com.iberdrola.practicas2026.MarPG.domain.model.ElectronicInvoice
import com.iberdrola.practicas2026.MarPG.ui.user_profile.ProfileState
import com.iberdrola.practicas2026.MarPG.ui.utils.UiText

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

    val resendAttempts: Int = 3,
    val lastResendTimestamp: Long = 0L,
    val remainingTime: String = "",

    val userProfile: ProfileState = ProfileState(),
    val formattedPhone: String = "",

    val showNoPhoneDialog: Boolean = false,
    val newPhoneInput: String = "",
    val passwordInput: String = "",
    val isPasswordVisible: Boolean = false,
    val phoneError: String? = null,

    val showSameEmailWarning: Boolean = false,

    val showNoAddressDialog: Boolean = false,
    val newAddressInput: String = "",
    val showDeactivationConfirmDialog: Boolean = false,
    val showSimulatedNotification: Boolean = false,
    val simulatedNotificationMessage: UiText = UiText.DynamicString(""),
    val simulatedOtpCode: String = "",
    
    val showNoAttemptsDialog: Boolean = false,
    val isNavigating: Boolean = false,

    val showPermissionDialog: Boolean = false,
    val isPermissionPermanentlyDenied: Boolean = false
)

enum class ElectronicInvoiceStep {
    SELECTION, FORM, VERIFICATION, SUCCESS
}