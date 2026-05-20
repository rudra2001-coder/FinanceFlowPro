package com.rudra.financeflowpro.worker

import android.content.Context
import android.os.Environment
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.rudra.financeflowpro.data.backup.BackupRepository
import com.rudra.financeflowpro.notification.NotificationHelper
import com.rudra.financeflowpro.util.AesEncryptor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupRepository: BackupRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val backupData = backupRepository.createFullBackupData()
            val json = backupRepository.toJson(backupData)

            // Use a simple XOR-based key derived from device identifier
            val encryptionKey = "FinanceFlow_Backup_Key_2025".toByteArray()
            val encrypted = AesEncryptor.encrypt(json, encryptionKey.copyOf(32))

            val fileName = "FinanceFlow_Backup_${LocalDate.now()}.json"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val financeFlowDir = File(downloadsDir, "FinanceFlow")
            financeFlowDir.mkdirs()
            val file = File(financeFlowDir, fileName)
            file.writeBytes(encrypted)

            backupRepository.cleanOldBackups(financeFlowDir, 30)
            notificationHelper.showBackupSuccess(fileName)
            Result.success()
        } catch (e: Exception) {
            notificationHelper.showBackupFailed(e.message ?: "Unknown error")
            Result.retry()
        }
    }
}

fun scheduleBackup(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    val request = PeriodicWorkRequestBuilder<BackupWorker>(24, TimeUnit.HOURS)
        .setConstraints(constraints)
        .setInitialDelay(calculateDelayUntil2AM(), TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "daily_backup",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
}

fun calculateDelayUntil2AM(): Long {
    val now = java.util.Calendar.getInstance()
    val target = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 2)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
        if (before(now)) add(java.util.Calendar.DAY_OF_MONTH, 1)
    }
    return target.timeInMillis - now.timeInMillis
}
