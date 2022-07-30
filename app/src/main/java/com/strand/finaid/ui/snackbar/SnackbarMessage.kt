package com.strand.finaid.ui.snackbar

import android.content.res.Resources
import androidx.annotation.StringRes
import com.strand.finaid.R

sealed class SnackbarMessage {

    class StringSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val actionPerformed: () -> Unit = {}
    ): SnackbarMessage()

    class ResourceSnackbar(
        @StringRes val message: Int,
        val actionLabel: String? = null,
        val actionPerformed: () -> Unit = {}
    ): SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resources: Resources): StringSnackbar {
            return when (this) {
                is StringSnackbar -> StringSnackbar(this.message, this.actionLabel, this.actionPerformed)
                is ResourceSnackbar -> StringSnackbar(resources.getString(this.message), this.actionLabel, this.actionPerformed)
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(R.string.generic_error)
        }
    }
}
