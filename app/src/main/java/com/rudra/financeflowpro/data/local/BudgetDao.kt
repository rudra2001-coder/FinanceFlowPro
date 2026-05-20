package com.rudra.financeflowpro.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetsForMonth(month: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE month = :month AND category = :category LIMIT 1")
    suspend fun getBudgetForCategory(month: String, category: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE month = :month")
    suspend fun getBudgetsForMonthOnce(month: String): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("UPDATE budgets SET spent = :spent WHERE id = :id")
    suspend fun updateSpent(id: Long, spent: Double)

    @Delete
    suspend fun delete(budget: BudgetEntity)
}
