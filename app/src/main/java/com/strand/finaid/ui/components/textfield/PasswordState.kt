package com.strand.finaid.ui.components.textfield


class PasswordState : TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)

class ConfirmPasswordState(private val passwordState: PasswordState) : TextFieldState() {
    override val isValid
        get() = passwordAndConfirmationValid(passwordState.text, text)

    override fun getError(): String = if (showErrors()) passwordConfirmationError() else ""
}

private fun passwordAndConfirmationValid(password: String, confirmedPassword: String): Boolean {
    return isPasswordValid(password) && password == confirmedPassword
}

private fun isPasswordValid(password: String): Boolean {
    return password.isNotBlank() && password.length >= MinPassLength
}

private fun passwordValidationError(password: String): String = "Fyll i lösenord med minst 6 tecken."

private fun passwordConfirmationError(): String = "Lösenorden måste matcha."

private const val MinPassLength = 6