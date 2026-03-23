package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import javax.inject.Inject

class FormatUserPhoneUseCase @Inject constructor() {
    operator fun invoke(phone: String): String {
        return if (phone.length >= 3) {
            "******${phone.takeLast(3)}"
        } else {
            "******"
        }
    }
}