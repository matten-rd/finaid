package com.strand.finaid.ui.components.textfield

import android.util.Patterns

class EmailState : TextFieldState(validator = ::isEmailValid, errorFor = ::emailValidationError)

private fun emailValidationError(email: String): String = "Fyll i en giltig mailadress."

private fun isEmailValid(email: String): Boolean =
    email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
