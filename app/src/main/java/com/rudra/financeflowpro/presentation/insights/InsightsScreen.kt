package com.rudra.financeflowpro.presentation.insights

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rudra.financeflowpro.domain.model.TipType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val healthScore by viewModel.healthScore.collectAsState()
    val spendingProfile by viewModel.spendingProfile.collectAsState()
    val tips by viewModel.tips.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Insights") },
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
            // Health Score
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        healthScore >= 80 -> Color(0xFF4CAF50)
                        healthScore >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFE53935)
                    }
                )
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Financial Health Score", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text(
                            when {
                                healthScore >= 80 -> "Excellent!"
                                healthScore >= 60 -> "Good"
                                healthScore >= 40 -> "Fair"
                                else -> "Needs Improvement"
                            },
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text("$healthScore", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                }
            }

            // 50-30-20 Analysis
            Text("50-30-20 Budget Rule", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            spendingProfile?.let { profile ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        BudgetCategoryCard("Needs\n50%", "${String.format("%.0f", profile.necessaryPercent)}%", profile.necessaryPercent, Color(0xFF2196F3))
                    }
                    Box(Modifier.weight(1f)) {
                        BudgetCategoryCard("Wants\n30%", "${String.format("%.0f", profile.discretionaryPercent)}%", profile.discretionaryPercent, Color(0xFFFF9800))
                    }
                    Box(Modifier.weight(1f)) {
                        BudgetCategoryCard("Savings\n20%", "${String.format("%.0f", profile.valuablePercent)}%", profile.valuablePercent, Color(0xFF4CAF50))
                    }
                }
            }

            // Tips
            Text("Financial Tips", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            tips.forEach { tip ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (tip.type) {
                            TipType.URGENT -> Color(0xFFFFEBEE)
                            TipType.WARNING -> Color(0xFFFFF3E0)
                            TipType.SUGGESTION -> Color(0xFFE3F2FD)
                            TipType.POSITIVE -> Color(0xFFE8F5E9)
                        }
                    )
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            when (tip.type) {
                                TipType.URGENT -> Icons.Default.Warning
                                TipType.WARNING -> Icons.Default.Info
                                TipType.SUGGESTION -> Icons.Default.Lightbulb
                                TipType.POSITIVE -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when (tip.type) {
                                TipType.URGENT -> Color(0xFFE53935)
                                TipType.WARNING -> Color(0xFFFF9800)
                                TipType.SUGGESTION -> Color(0xFF2196F3)
                                TipType.POSITIVE -> Color(0xFF4CAF50)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(tip.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (tips.isEmpty() && spendingProfile != null) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Great job! No specific tips right now.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun BudgetCategoryCard(label: String, value: String, percent: Double, color: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (percent / 100).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = color,
            )
        }
    }
}
