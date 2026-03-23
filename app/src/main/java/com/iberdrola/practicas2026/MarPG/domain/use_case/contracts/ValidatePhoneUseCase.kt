package com.iberdrola.practicas2026.MarPG.domain.use_case.contracts

import javax.inject.Inject

class ValidatePhoneUseCase @Inject constructor() {
    operator fun invoke(phone: String): Boolean {
        return phone.length == 9 && phone.all { it.isDigit() }
    }
}