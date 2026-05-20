package com.rudra.financeflowpro.data.backup

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rudra.financeflowpro.data.local.*
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val version: String = "1.0",
    val appVersion: String = "1.0",
    val backupDate: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val data: BackupDataSection
)

data class BackupDataSection(
    val accounts: List<AccountEntity>,
    val transactions: List<TransactionEntity>,
    val savingsGoals: List<SavingsGoalEntity>,
    val investments: List<InvestmentEntity>,
    val budgets: List<BudgetEntity>
)

@Singleton
class BackupRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val investmentDao: InvestmentDao,
    private val budgetDao: BudgetDao
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun createFullBackupData(): BackupData {
        return BackupData(
            data = BackupDataSection(
                accounts = accountDao.getAllAccountsOnce(),
                transactions = transactionDao.getTransactionsInRangeOnce(0, Long.MAX_VALUE),
                savingsGoals = savingsGoalDao.getActiveGoals(),
                investments = investmentDao.getAllInvestments().let { /* use once query */ emptyList() },
                budgets = budgetDao.getBudgetsForMonthOnce(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
            )
        )
    }

    fun toJson(backupData: BackupData): String = gson.toJson(backupData)

    fun fromJson(json: String): BackupData = gson.fromJson(json, BackupData::class.java)

    fun cleanOldBackups(directory: File, maxAgeDays: Int = 30) {
        val cutoff = System.currentTimeMillis() - (maxAgeDays * 24L * 60 * 60 * 1000)
        directory.listFiles()?.forEach { file ->
            if (file.name.startsWith("FinanceFlow_Backup") && file.lastModified() < cutoff) {
                file.delete()
            }
        }
    }

    suspend fun restoreFromBackup(backupData: BackupData) {
        backupData.data.accounts.forEach { accountDao.insert(it) }
        backupData.data.transactions.forEach { transactionDao.insert(it) }
        backupData.data.savingsGoals.forEach { savingsGoalDao.insert(it) }
        backupData.data.investments.forEach { investmentDao.insert(it) }
        backupData.data.budgets.forEach { budgetDao.insert(it) }
    }
}
