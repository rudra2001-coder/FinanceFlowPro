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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScoreScreen(
    onNavigateBack: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val healthScore by viewModel.healthScore.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Health Score") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { healthScore / 100f },
                    modifier = Modifier.size(200.dp),
                    strokeWidth = 16.dp,
                    color = when {
                        healthScore >= 80 -> Color(0xFF4CAF50)
                        healthScore >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFE53935)
                    }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$healthScore", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    Text("/ 100", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                when {
                    healthScore >= 80 -> "Excellent Financial Health!"
                    healthScore >= 60 -> "Good - Room for Improvement"
                    healthScore >= 40 -> "Fair - Needs Attention"
                    else -> "Needs Significant Improvement"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Text("How the score is calculated:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            ScoreBreakdown("Savings Rate", "25 pts", "Based on your savings percentage of income")
            ScoreBreakdown("Budget Adherence", "25 pts", "How well you stay within budget limits")
            ScoreBreakdown("Expense Consistency", "20 pts", "Month-to-month spending stability")
            ScoreBreakdown("Debt Ratio", "15 pts", "Credit usage and debt management")
            ScoreBreakdown("Investment Activity", "15 pts", "Active investing for long-term growth")

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Insights")
            }
        }
    }
}

@Composable
fun ScoreBreakdown(title: String, points: String, description: String) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(points, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
