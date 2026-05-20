package com.rudra.financeflowpro.domain.repository

import com.rudra.financeflowpro.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface SavingsGoalRepository {
    fun getAllGoals(): Flow<List<SavingsGoal>>
    fun getGoalById(id: Long): Flow<SavingsGoal?>
    suspend fun getGoalByIdOnce(id: Long): SavingsGoal?
    suspend fun insert(goal: SavingsGoal): Long
    suspend fun update(goal: SavingsGoal)
    suspend fun delete(goal: SavingsGoal)
    suspend fun addContribution(goalId: Long, amount: Double)
    suspend fun getActiveGoals(): List<SavingsGoal>
}
