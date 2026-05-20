package com.rudra.financeflowpro.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.rudra.financeflowpro.domain.model.Transaction
import java.io.File

class CsvExporter(private val context: Context) {

    fun exportTransactions(transactions: List<Transaction>, fileName: String): Uri {
        val csv = buildString {
            appendLine("Date,Time,Account,Type,Category,Subcategory,Amount,Description,Note,Tags")
            transactions.forEach { t ->
                appendLine(
                    "${DateUtils.formatDate(t.date)}," +
                            "${DateUtils.formatTime(t.time)}," +
                            "${t.accountId}," +
                            "${t.type}," +
                            "${t.category}," +
                            "${t.subcategory}," +
                            "${t.amount}," +
                            "\"${t.description.replace("\"", "\"\"")}\"," +
                            "\"${t.note.replace("\"", "\"\"")}\"," +
                            "${t.tags}"
                )
            }
        }

        val dir = File(context.getExternalFilesDir(null), "exports")
        dir.mkdirs()
        val file = File(dir, fileName)
        file.writeText(csv)
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    }

    fun exportToDownloads(fileName: String, content: String): File {
        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )
        val dir = File(downloadsDir, "FinanceFlow")
        dir.mkdirs()
        val file = File(dir, fileName)
        file.writeText(content)
        return file
    }
}
