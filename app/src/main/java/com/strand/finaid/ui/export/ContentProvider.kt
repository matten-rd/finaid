package com.strand.finaid.ui.export

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

object MimeTypes {
    const val XLS = "application/vnd.ms-excel"
    const val XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val CSV = "text/comma-separated-values"
}


class OpenDocumentActivityResult(
    private val launcher: ManagedActivityResultLauncher<Array<String>, Uri?>,
    val uri: Uri?
) {
    fun launch(mimeTypes: Array<String>) {
        launcher.launch(mimeTypes)
    }
}

@Composable
fun rememberOpenDocumentActivityResult(
    initialUri: Uri? = null,
    onSuccess: (String) -> Unit,
    onError: () -> Unit
): OpenDocumentActivityResult {
    var uri by rememberSaveable { mutableStateOf<Uri?>(initialUri) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            uri = it
            onSuccess(uri.toString())
        } else {
            onError()
        }
    }

    return remember(launcher, uri) {
        OpenDocumentActivityResult(launcher, uri)
    }
}