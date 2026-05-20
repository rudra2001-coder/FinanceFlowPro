package com.rudra.financeflowpro.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    data object Accounts : Screen("accounts", "Accounts", Icons.Default.AccountBalance)
    data object AddAccount : Screen("add_account", "Add Account")
    data object EditAccount : Screen("edit_account/{accountId}", "Edit Account") {
        fun createRoute(accountId: Long) = "edit_account/$accountId"
    }
    data object AccountDetail : Screen("account_detail/{accountId}", "Account Detail") {
        fun createRoute(accountId: Long) = "account_detail/$accountId"
    }
    data object Transactions : Screen("transactions", "Transactions", Icons.Default.Assessment)
    data object AddTransaction : Screen("add_transaction", "Add Transaction")
    data object EditTransaction : Screen("edit_transaction/{transactionId}", "Edit Transaction") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    data object Transfer : Screen("transfer", "Transfer")
    data object Savings : Screen("savings", "Savings", Icons.Default.Savings)
    data object AddSavingsGoal : Screen("add_savings_goal", "New Savings Goal")
    data object EditSavingsGoal : Screen("edit_savings_goal/{goalId}", "Edit Goal") {
        fun createRoute(goalId: Long) = "edit_savings_goal/$goalId"
    }
    data object GoalDetail : Screen("goal_detail/{goalId}", "Goal Detail") {
        fun createRoute(goalId: Long) = "goal_detail/$goalId"
    }
    data object Investments : Screen("investments", "Investments", Icons.Default.TrendingUp)
    data object AddInvestment : Screen("add_investment", "Add Investment")
    data object EditInvestment : Screen("edit_investment/{investmentId}", "Edit Investment") {
        fun createRoute(investmentId: Long) = "edit_investment/$investmentId"
    }
    data object InvestmentDetail : Screen("investment_detail/{investmentId}", "Investment Detail") {
        fun createRoute(investmentId: Long) = "investment_detail/$investmentId"
    }
    data object Reports : Screen("reports", "Reports")
    data object IncomeReport : Screen("income_report", "Income Report")
    data object ExpenseReport : Screen("expense_report", "Expense Report")
    data object CashFlowReport : Screen("cashflow_report", "Cash Flow")
    data object InvestmentReport : Screen("investment_report", "Investment Report")
    data object Insights : Screen("insights", "Insights")
    data object HealthScore : Screen("health_score", "Financial Health")
    data object Budget : Screen("budget", "Budget")
    data object AddBudget : Screen("add_budget", "Set Budget")
    data object Settings : Screen("settings", "Settings")
    data object BackupRestore : Screen("backup_restore", "Backup & Restore")
    data object Search : Screen("search", "Search")

    companion object {
        val bottomNavItems = listOf(Dashboard, Accounts, Transactions, Savings, Investments)
    }
}
