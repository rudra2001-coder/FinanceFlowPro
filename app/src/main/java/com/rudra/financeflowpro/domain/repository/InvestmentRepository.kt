package com.rudra.financeflowpro.domain.repository

import com.rudra.financeflowpro.domain.model.Investment
import kotlinx.coroutines.flow.Flow

interface InvestmentRepository {
    fun getAllInvestments(): Flow<List<Investment>>
    fun getInvestmentById(id: Long): Flow<Investment?>
    suspend fun getInvestmentByIdOnce(id: Long): Investment?
    suspend fun insert(investment: Investment): Long
    suspend fun update(investment: Investment)
    suspend fun delete(investment: Investment)
    fun getTotalInvested(): Flow<Double?>
    fun getTotalCurrentValue(): Flow<Double?>
    fun getInvestmentsByType(): Flow<List<Pair<String, Double>>>
}
