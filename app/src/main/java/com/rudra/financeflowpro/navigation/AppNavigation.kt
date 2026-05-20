package com.rudra.financeflowpro.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rudra.financeflowpro.presentation.accounts.*
import com.rudra.financeflowpro.presentation.budget.*
import com.rudra.financeflowpro.presentation.dashboard.*
import com.rudra.financeflowpro.presentation.insights.*
import com.rudra.financeflowpro.presentation.investments.*
import com.rudra.financeflowpro.presentation.reports.*
import com.rudra.financeflowpro.presentation.savings.*
import com.rudra.financeflowpro.presentation.settings.*
import com.rudra.financeflowpro.presentation.transactions.*

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = Screen.bottomNavItems.map { screen ->
        BottomNavItem(screen, screen.icon!!, screen.title)
    }

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route in listOf(Screen.Dashboard.route, Screen.Transactions.route)) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTransaction.route) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAccount = { navController.navigate(Screen.Accounts.route) },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                    onNavigateToAccountDetail = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) },
                    onNavigateToGoalDetail = { id -> navController.navigate(Screen.GoalDetail.createRoute(id)) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onNavigateToInsights = { navController.navigate(Screen.Insights.route) }
                )
            }

            composable(Screen.Accounts.route) {
                AccountsScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddAccount.route) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.AccountDetail.createRoute(id)) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditAccount.createRoute(id)) },
                    onNavigateToTransfer = { navController.navigate(Screen.Transfer.route) }
                )
            }

            composable(Screen.AddAccount.route) {
                AddEditAccountScreen(
                    accountId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditAccount.route,
                arguments = listOf(navArgument("accountId") { type = NavType.LongType })
            ) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getLong("accountId") ?: return@composable
                AddEditAccountScreen(
                    accountId = accountId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AccountDetail.route,
                arguments = listOf(navArgument("accountId") { type = NavType.LongType })
            ) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getLong("accountId") ?: return@composable
                AccountDetailScreen(
                    accountId = accountId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) },
                    onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddTransaction.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditTransaction.createRoute(id)) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) }
                )
            }

            composable(Screen.AddTransaction.route) {
                AddEditTransactionScreen(
                    transactionId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
                AddEditTransactionScreen(
                    transactionId = transactionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Transfer.route) {
                TransferScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Savings.route) {
                SavingsGoalsScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddSavingsGoal.route) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.GoalDetail.createRoute(id)) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditSavingsGoal.createRoute(id)) }
                )
            }

            composable(Screen.AddSavingsGoal.route) {
                AddEditSavingsGoalScreen(
                    goalId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditSavingsGoal.route,
                arguments = listOf(navArgument("goalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getLong("goalId") ?: return@composable
                AddEditSavingsGoalScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.GoalDetail.route,
                arguments = listOf(navArgument("goalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getLong("goalId") ?: return@composable
                GoalDetailScreen(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Investments.route) {
                InvestmentsScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddInvestment.route) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.InvestmentDetail.createRoute(id)) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EditInvestment.createRoute(id)) }
                )
            }

            composable(Screen.AddInvestment.route) {
                AddEditInvestmentScreen(
                    investmentId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditInvestment.route,
                arguments = listOf(navArgument("investmentId") { type = NavType.LongType })
            ) { backStackEntry ->
                val investmentId = backStackEntry.arguments?.getLong("investmentId") ?: return@composable
                AddEditInvestmentScreen(
                    investmentId = investmentId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.InvestmentDetail.route,
                arguments = listOf(navArgument("investmentId") { type = NavType.LongType })
            ) { backStackEntry ->
                val investmentId = backStackEntry.arguments?.getLong("investmentId") ?: return@composable
                InvestmentDetailScreen(
                    investmentId = investmentId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reports.route) {
                ReportsHomeScreen(
                    onNavigateToIncomeReport = { navController.navigate(Screen.IncomeReport.route) },
                    onNavigateToExpenseReport = { navController.navigate(Screen.ExpenseReport.route) },
                    onNavigateToCashFlowReport = { navController.navigate(Screen.CashFlowReport.route) },
                    onNavigateToInvestmentReport = { navController.navigate(Screen.InvestmentReport.route) }
                )
            }

            composable(Screen.IncomeReport.route) { IncomeReportScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Screen.ExpenseReport.route) { ExpenseReportScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Screen.CashFlowReport.route) { CashFlowReportScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Screen.InvestmentReport.route) { InvestmentReportScreen(onNavigateBack = { navController.popBackStack() }) }

            composable(Screen.Insights.route) { InsightsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Screen.HealthScore.route) { HealthScoreScreen(onNavigateBack = { navController.popBackStack() }) }

            composable(Screen.Budget.route) {
                BudgetScreen(
                    onNavigateToAdd = { navController.navigate(Screen.AddBudget.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AddBudget.route) { AddBudgetScreen(onNavigateBack = { navController.popBackStack() }) }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToBackup = { navController.navigate(Screen.BackupRestore.route) }
                )
            }

            composable(Screen.BackupRestore.route) { BackupRestoreScreen(onNavigateBack = { navController.popBackStack() }) }
            composable(Screen.Search.route) { SearchScreen(onNavigateBack = { navController.popBackStack() }) }
        }
    }
}
