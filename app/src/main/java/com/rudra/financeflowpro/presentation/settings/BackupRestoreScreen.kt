package com.rudra.financeflowpro.presentation.settings

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isBackingUp by viewModel.isBackingUp.collectAsState()
    val backupStatus by viewModel.backupStatus.collectAsState()

    var backupFiles by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "FinanceFlow")
        if (dir.exists()) {
            backupFiles = dir.listFiles()
                ?.filter { it.name.startsWith("FinanceFlow_Backup") }
                ?.sortedByDescending { it.lastModified() }
                ?.map { it.name } ?: emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Backup your data to local storage. All files are encrypted.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Button(
                onClick = { viewModel.triggerBackup() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBackingUp
            ) {
                if (isBackingUp) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Create Backup Now")
            }

            OutlinedButton(
                onClick = { viewModel.scheduleDailyBackup() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Schedule Daily Backup (2 AM)")
            }

            backupStatus?.let {
                Card(Modifier.fillMaxWidth()) {
                    Text(it, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Divider()

            Text("Available Backups", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (backupFiles.isEmpty()) {
                Text("No backups found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                backupFiles.forEach { name ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(name, style = MaterialTheme.typography.bodyMedium)
                            }
                            TextButton(onClick = { /* TODO: implement restore */ }) {
                                Text("Restore")
                            }
                        }
                    }
                }
            }
        }
    }
}
