package com.strand.finaid.ext

import android.util.Patterns
import java.text.DecimalFormat

private const val MinPassLength = 6
private const val PassPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"
private val AmountDecimalFormat = DecimalFormat("#,###")

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank()
            && this.length >= MinPassLength
            //&& Pattern.compile(PASS_PATTERN).matcher(this).matches()
}

fun String.passwordMatches(repeated: String): Boolean {
    return this == repeated
}

fun String.idFromParameter(): String {
    return this.substring(1, this.length-1)
}

fun Int.formatAmount(): String {
    return AmountDecimalFormat.format(this)
}

