package com.rudra.financeflowpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        SavingsGoalEntity::class,
        InvestmentEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinanceFlowDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceFlowDatabase? = null

        fun getInstance(context: Context): FinanceFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): FinanceFlowDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FinanceFlowDatabase::class.java,
                "financeflow_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
