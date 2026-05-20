# FinanceFlow Pro — Codebase Reference

## Tech Stack
- **Language:** Kotlin 100%
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture (data/domain/presentation layers)
- **Database:** Room (SQLite) — fully offline
- **DI:** Hilt
- **Async:** Kotlin Coroutines + Flow
- **Navigation:** Jetpack Navigation Compose
- **Charts:** MPAndroidChart / compose-charts
- **Backup:** WorkManager + AES encryption
- **Build:** Gradle KTS with version catalog (libs.versions.toml)

## Package Structure

```
com.rudra.financeflowpro/
├── data/                    # Data layer
│   ├── local/               # Room entities, DAOs, database
│   ├── repository/          # Repository implementations
│   └── backup/              # Backup repository (JSON serialization)
├── domain/                  # Domain layer (pure Kotlin)
│   ├── model/               # Domain models + enums
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Business logic use cases
├── presentation/            # UI layer (Compose + ViewModels)
│   ├── dashboard/           # Home screen
│   ├── accounts/            # Account CRUD + detail
│   ├── transactions/        # Transaction list, add/edit, transfer, search
│   ├── savings/             # Savings goals
│   ├── investments/         # Investment portfolio
│   ├── reports/             # Income/expense/cashflow/investment reports
│   ├── insights/            # Spending analysis, health score, tips
│   ├── budget/              # Monthly budget management
│   └── settings/            # Settings + backup/restore
├── navigation/              # Screen routes + NavHost
├── di/                      # Hilt modules
├── worker/                  # WorkManager workers
├── notification/            # Notification channels & helpers
└── util/                    # Currency, date, CSV, encryption helpers
```

## Data Flow

```
UI (Compose Screen)
  ↓ collects StateFlow
ViewModel
  ↓ calls suspend fun / Flow
UseCase (domain layer - business logic)
  ↓
Repository Interface (domain)
  ↓
Repository Impl (data layer)
  ↓
Room DAO → SQLite
```

## Key Patterns

### 1. ViewModels
- All ViewModels are `@HiltViewModel` with `@Inject constructor`
- Expose `StateFlow` for UI state, `MutableStateFlow` internally
- Use `viewModelScope.launch` for async work
- Sample: `DashboardViewModel.kt`

### 2. Room Database
- `FinanceFlowDatabase` singleton via `getInstance()`
- 5 entities: `AccountEntity`, `TransactionEntity`, `SavingsGoalEntity`, `InvestmentEntity`, `BudgetEntity`
- Transactions have FK to accounts with CASCADE delete
- All DAOs return `Flow<>` for reactive UI updates

### 3. Navigation
- `Screen` sealed class defines all 28 routes
- `AppNavigation.kt` has full `NavHost` with bottom nav bar (Dashboard, Accounts, Transactions, Savings, Investments)
- FAB appears on Dashboard and Transactions screens for quick-add

### 4. Backup System
- `BackupWorker` runs daily at 2 AM via WorkManager `PeriodicWorkRequest`
- Encrypts JSON with AES/GCM before writing to `Downloads/FinanceFlow/`
- `BackupRepository` handles create/restore/cleanup
- Old backups >30 days auto-deleted

### 5. Transfer Logic
- `TransferBetweenAccountsUseCase` creates debit + credit transactions atomically
- Updates both account balances simultaneously
- Category: "Transfer Out" / "Transfer In"

### 6. Financial Health Score (0-100)
- Savings Rate (0-25 pts): >20% = 25, 10-20% = 15, <10% = 5
- Budget Adherence (0-25 pts): under budget = 25, 1-2 over = 15
- Expense Consistency (0-20 pts): stable = 20
- Debt Ratio (0-15 pts): no credit overuse = 15
- Investment Activity (0-15 pts): active = 15

### 7. Spending Classification
- NECESSARY: Food, Transport, Bills, Health, Education
- VALUABLE: Investment, Savings, Skill development
- DISCRETIONARY: Entertainment, Shopping, Dining
- WASTEFUL: Excessive/impulse spending above threshold

### 8. Notifications (6 channels)
- `backup_channel` — Daily backup success/failure
- `budget_channel` — 80%/100% budget alerts
- `savings_channel` — Goal milestones (25/50/75/100%)
- `general_channel` — Daily summary, low balance
- `investment_channel` — Maturity alerts
- `recurring_channel` — Upcoming recurring payments

## Adding a New Feature

1. Add entity to `data/local/` and DAO methods
2. Add domain model to `domain/model/`
3. Add/update repository interface in `domain/repository/`
4. Add use case in `domain/usecase/` if business logic needed
5. Implement repository in `data/repository/`
6. Add DI bindings in `di/AppModule.kt`
7. Create ViewModel in `presentation/<feature>/`
8. Create Composable screens in same package
9. Add route to `navigation/Screen.kt`
10. Add composable destination to `navigation/AppNavigation.kt`

## Build & Run
- Open project in Android Studio
- Build: `./gradlew assembleDebug`
- Min SDK: 28, Target SDK: 36
- No internet permission required (fully offline)
