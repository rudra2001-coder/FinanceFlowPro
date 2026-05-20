package com.rudra.financeflowpro.data.repository

import com.rudra.financeflowpro.data.local.SavingsGoalDao
import com.rudra.financeflowpro.data.local.SavingsGoalEntity
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavingsGoalRepositoryImpl @Inject constructor(
    private val savingsGoalDao: SavingsGoalDao
) : SavingsGoalRepository {

    private fun SavingsGoalEntity.toDomain() = SavingsGoal(
        id = id, name = name, targetAmount = targetAmount,
        currentAmount = currentAmount, deadline = deadline,
        linkedAccountId = linkedAccountId, color = color,
        icon = icon, isCompleted = isCompleted,
        autoContributeAmount = autoContributeAmount,
        autoContributeInterval = autoContributeInterval, createdAt = createdAt
    )

    private fun SavingsGoal.toEntity() = SavingsGoalEntity(
        id = id, name = name, targetAmount = targetAmount,
        currentAmount = currentAmount, deadline = deadline,
        linkedAccountId = linkedAccountId, color = color,
        icon = icon, isCompleted = isCompleted,
        autoContributeAmount = autoContributeAmount,
        autoContributeInterval = autoContributeInterval, createdAt = createdAt
    )

    override fun getAllGoals(): Flow<List<SavingsGoal>> =
        savingsGoalDao.getAllGoals().map { list -> list.map { it.toDomain() } }

    override fun getGoalById(id: Long): Flow<SavingsGoal?> =
        savingsGoalDao.getGoalById(id).map { it?.toDomain() }

    override suspend fun getGoalByIdOnce(id: Long): SavingsGoal? =
        savingsGoalDao.getGoalByIdOnce(id)?.toDomain()

    override suspend fun insert(goal: SavingsGoal): Long =
        savingsGoalDao.insert(goal.toEntity())

    override suspend fun update(goal: SavingsGoal) =
        savingsGoalDao.update(goal.toEntity())

    override suspend fun delete(goal: SavingsGoal) =
        savingsGoalDao.delete(goal.toEntity())

    override suspend fun addContribution(goalId: Long, amount: Double) =
        savingsGoalDao.addContribution(goalId, amount)

    override suspend fun getActiveGoals(): List<SavingsGoal> =
        savingsGoalDao.getActiveGoals().map { it.toDomain() }
}
