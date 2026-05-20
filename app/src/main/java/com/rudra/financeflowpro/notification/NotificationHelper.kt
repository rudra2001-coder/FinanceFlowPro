package com.rudra.financeflowpro.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.rudra.financeflowpro.MainActivity
import com.rudra.financeflowpro.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_BACKUP = "backup_channel"
        const val CHANNEL_BUDGET = "budget_channel"
        const val CHANNEL_SAVINGS = "savings_channel"
        const val CHANNEL_GENERAL = "general_channel"
        const val CHANNEL_INVESTMENT = "investment_channel"
        const val CHANNEL_RECURRING = "recurring_channel"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(CHANNEL_BACKUP, "Backup", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Daily backup notifications"
            },
            NotificationChannel(CHANNEL_BUDGET, "Budget Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Budget limit warnings and alerts"
            },
            NotificationChannel(CHANNEL_SAVINGS, "Savings Goals", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Savings goal milestones"
            },
            NotificationChannel(CHANNEL_GENERAL, "General", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Daily and weekly summaries"
            },
            NotificationChannel(CHANNEL_INVESTMENT, "Investments", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Investment maturity alerts"
            },
            NotificationChannel(CHANNEL_RECURRING, "Recurring", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Recurring transaction reminders"
            }
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channels.forEach { manager.createNotificationChannel(it) }
    }

    private fun showNotification(channelId: String, title: String, message: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    fun showBackupSuccess(fileName: String) {
        showNotification(CHANNEL_BACKUP, "Backup Successful",
            "Daily backup saved: $fileName", 1001)
    }

    fun showBackupFailed(error: String) {
        showNotification(CHANNEL_BACKUP, "Backup Failed",
            "Error: $error. Will retry.", 1002)
    }

    fun showBudgetWarning(category: String, percent: Int) {
        showNotification(CHANNEL_BUDGET, "Budget Alert",
            "$category: $percent% used", 2001)
    }

    fun showBudgetExceeded(category: String) {
        showNotification(CHANNEL_BUDGET, "Budget Exceeded",
            "$category budget has been exceeded!", 2002)
    }

    fun showSavingsMilestone(goalName: String, percent: Int) {
        showNotification(CHANNEL_SAVINGS, "Savings Milestone 🎉",
            "$goalName is $percent% complete!", 3001)
    }

    fun showDailySummary(totalSpend: Double, categoryCount: Int) {
        showNotification(CHANNEL_GENERAL, "Daily Summary",
            "Today's spend: ${String.format("%.2f", totalSpend)} in $categoryCount categories", 4001)
    }

    fun showLowBalance(accountName: String, balance: Double) {
        showNotification(CHANNEL_GENERAL, "Low Balance Warning",
            "$accountName balance: ${String.format("%.2f", balance)}", 4002)
    }

    fun showInvestmentMaturity(investmentName: String, daysLeft: Int) {
        showNotification(CHANNEL_INVESTMENT, "Investment Maturity",
            "$investmentName matures in $daysLeft days", 5001)
    }

    fun showRecurringReminder(description: String, amount: Double) {
        showNotification(CHANNEL_RECURRING, "Upcoming Payment",
            "$description of ${String.format("%.2f", amount)} in 2 days", 6001)
    }
}
