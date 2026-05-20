package com.rudra.financeflowpro.data.repository

import com.rudra.financeflowpro.data.local.BudgetDao
import com.rudra.financeflowpro.data.local.BudgetEntity
import com.rudra.financeflowpro.domain.model.Budget
import com.rudra.financeflowpro.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    private fun BudgetEntity.toDomain() = Budget(
        id = id, category = category, amount = amount,
        spent = spent, month = month, createdAt = createdAt
    )

    private fun Budget.toEntity() = BudgetEntity(
        id = id, category = category, amount = amount,
        spent = spent, month = month, createdAt = createdAt
    )

    override fun getBudgetsForMonth(month: String): Flow<List<Budget>> =
        budgetDao.getBudgetsForMonth(month).map { list -> list.map { it.toDomain() } }

    override suspend fun getBudgetForCategory(month: String, category: String): Budget? =
        budgetDao.getBudgetForCategory(month, category)?.toDomain()

    override suspend fun getBudgetsForMonthOnce(month: String): List<Budget> =
        budgetDao.getBudgetsForMonthOnce(month).map { it.toDomain() }

    override suspend fun insert(budget: Budget): Long =
        budgetDao.insert(budget.toEntity())

    override suspend fun update(budget: Budget) =
        budgetDao.update(budget.toEntity())

    override suspend fun delete(budget: Budget) =
        budgetDao.delete(budget.toEntity())
}
