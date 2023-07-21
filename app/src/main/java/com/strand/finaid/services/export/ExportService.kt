package com.strand.finaid.services.export

import androidx.annotation.WorkerThread
import com.strand.finaid.data.models.Transaction
import com.strand.finaid.ext.asHexCode
import com.strand.finaid.ext.formatDayMonthYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.util.AreaReference
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ExportService {

    fun exportTransactions(
        type: Exports,
        content: List<Transaction>,
    ): Flow<Boolean> = when(type) {
        is Exports.XLSX -> writeTransactionsToExcel(type.exportConfig, content)
    }

    @WorkerThread
    private fun writeTransactionsToExcel(exportConfig: ExportConfig, content: List<Transaction>) = flow<Boolean> {
        with(exportConfig) {
            hostPath.ifEmpty { throw IllegalStateException("Wrong path!") }

            val hostDirectory = File(hostPath)
            if (!hostDirectory.exists()) {
                hostDirectory.mkdir()
            }

            val filename = "$fileName.xlsx"
            val file = File("${hostDirectory.path}/${filename}")

            var fis: FileInputStream? = null
            val wb = if (file.exists()) {
                fis = FileInputStream(file)
                XSSFWorkbook(fis)
            } else {
                XSSFWorkbook()
            }

            val sheetName = WorkbookUtil.createSafeSheetName(sheetName)
            val sheet = if (wb.getSheetIndex(sheetName) == -1)
                wb.createSheet(sheetName)
            else
                wb.getSheet(sheetName)

            content.forEachIndexed { index, transaction ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(transaction.memo)
                row.createCell(1).setCellValue(transaction.category.name)
                row.createCell(2).setCellValue(transaction.date.formatDayMonthYear())
                row.createCell(3).setCellValue(transaction.amount.toDouble())
                row.createCell(4).setCellValue(transaction.lastModified.formatDayMonthYear())
                row.createCell(5).setCellValue(transaction.category.color.asHexCode())
            }
            val headers = listOf("Memo", "Category", "Date", "Amount", "Last modified", "Color HEX")
            // If there already exists a table we should not create a new one as this would require
            // us to modify both the cell content of the header and the content of the header in the
            // Excel Table object
            val shouldCreateTable = sheet.tables.isEmpty()
            if (shouldCreateTable) createTable(sheet, content.size, headers)

            val fos = FileOutputStream(file)
            fis?.close()
            wb.write(fos)
            fos.flush()
            fos.close()
            wb.close()
        }
        emit(true)
    }.flowOn(Dispatchers.IO)


    private fun createTable(sheet: XSSFSheet, numRows: Int, headers: List<String>) {
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            headerRow.createCell(index).setCellValue(header)
        }

        val numCols = headers.size - 1
        val areaReference = AreaReference(
            CellReference(0, 0), CellReference(numRows, numCols), SpreadsheetVersion.EXCEL2007
        )

        // creates a table having numCols columns as of area reference but all of those have id 1, so we need repairing
        val table = sheet.createTable(areaReference)
        (1..numCols).forEach {
            table.ctTable.tableColumns.getTableColumnArray(it).id = (it + 1).toLong()
        }

        table.name = sheet.sheetName
        table.displayName = sheet.sheetName + "_table"

        // For now, create the initial style in a low-level way
        table.ctTable.addNewTableStyleInfo()
        val style = table.style as XSSFTableStyleInfo
        style.name = "TableStyleMedium2"
        style.isShowColumnStripes = false
        style.isShowRowStripes = true

        table.ctTable.addNewAutoFilter().ref = table.area.formatAsString()
    }
}