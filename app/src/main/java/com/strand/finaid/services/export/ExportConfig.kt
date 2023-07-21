package com.strand.finaid.services.export

import android.os.Environment
import com.strand.finaid.ext.formatDayMonthYear
import com.strand.finaid.ext.formatMonthYear
import java.time.Instant
import java.util.*

sealed class Exports {
    data class XLSX(val exportConfig: ExportConfig) : Exports()
}

data class ExportConfig(
    val fileName: String = "finaid_transactions",
    val hostPath: String = Environment.getExternalStorageDirectory()?.absolutePath?.plus("/Documents/Finaid") ?: "",
    val sheetName: String
)
