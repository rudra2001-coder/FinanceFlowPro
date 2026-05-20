package com.rudra.financeflowpro.di

import android.content.Context
import androidx.room.Room
import com.rudra.financeflowpro.data.local.*
import com.rudra.financeflowpro.data.repository.*
import com.rudra.financeflowpro.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinanceFlowDatabase {
        return FinanceFlowDatabase.getInstance(context)
    }

    @Provides
    fun provideAccountDao(db: FinanceFlowDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideTransactionDao(db: FinanceFlowDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideSavingsGoalDao(db: FinanceFlowDatabase): SavingsGoalDao = db.savingsGoalDao()

    @Provides
    fun provideInvestmentDao(db: FinanceFlowDatabase): InvestmentDao = db.investmentDao()

    @Provides
    fun provideBudgetDao(db: FinanceFlowDatabase): BudgetDao = db.budgetDao()

    @Provides
    @Singleton
    fun provideAccountRepository(impl: AccountRepositoryImpl): AccountRepository = impl

    @Provides
    @Singleton
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl

    @Provides
    @Singleton
    fun provideSavingsGoalRepository(impl: SavingsGoalRepositoryImpl): SavingsGoalRepository = impl

    @Provides
    @Singleton
    fun provideInvestmentRepository(impl: InvestmentRepositoryImpl): InvestmentRepository = impl

    @Provides
    @Singleton
    fun provideBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository = impl
}
