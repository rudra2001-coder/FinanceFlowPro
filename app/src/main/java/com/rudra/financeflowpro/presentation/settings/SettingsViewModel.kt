package com.rudra.financeflowpro.presentation.settings

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.data.backup.BackupRepository
import com.rudra.financeflowpro.util.AesEncryptor
import com.rudra.financeflowpro.worker.scheduleBackup
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _isBackingUp = MutableStateFlow(false)
    val isBackingUp: StateFlow<Boolean> = _isBackingUp.asStateFlow()

    private val _backupStatus = MutableStateFlow<String?>(null)
    val backupStatus: StateFlow<String?> = _backupStatus.asStateFlow()

    fun triggerBackup() {
        viewModelScope.launch {
            _isBackingUp.value = true
            try {
                val backupData = backupRepository.createFullBackupData()
                val json = backupRepository.toJson(backupData)
                val key = "FinanceFlow_Backup_Key_2025".toByteArray()
                val encrypted = AesEncryptor.encrypt(json, key.copyOf(32))
                val fileName = "FinanceFlow_Backup_${LocalDate.now()}.json"
                val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "FinanceFlow")
                dir.mkdirs()
                File(dir, fileName).writeBytes(encrypted)
                _backupStatus.value = "Backup saved: $fileName"
            } catch (e: Exception) {
                _backupStatus.value = "Backup failed: ${e.message}"
            }
            _isBackingUp.value = false
        }
    }

    fun scheduleDailyBackup() {
        scheduleBackup(context)
        _backupStatus.value = "Daily backup scheduled (2:00 AM)"
    }
}
