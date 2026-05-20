package com.rudra.financeflowpro.data.repository

import com.rudra.financeflowpro.data.local.InvestmentDao
import com.rudra.financeflowpro.data.local.InvestmentEntity
import com.rudra.financeflowpro.domain.model.Investment
import com.rudra.financeflowpro.domain.model.InvestmentType
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvestmentRepositoryImpl @Inject constructor(
    private val investmentDao: InvestmentDao
) : InvestmentRepository {

    private fun InvestmentEntity.toDomain() = Investment(
        id = id, name = name, type = InvestmentType.valueOf(type),
        amountInvested = amountInvested, currentValue = currentValue,
        units = units, buyPrice = buyPrice, currentPrice = currentPrice,
        linkedAccountId = linkedAccountId, startDate = startDate,
        maturityDate = maturityDate, returnRate = returnRate,
        notes = notes, createdAt = createdAt
    )

    private fun Investment.toEntity() = InvestmentEntity(
        id = id, name = name, type = type.name,
        amountInvested = amountInvested, currentValue = currentValue,
        units = units, buyPrice = buyPrice, currentPrice = currentPrice,
        linkedAccountId = linkedAccountId, startDate = startDate,
        maturityDate = maturityDate, returnRate = returnRate,
        notes = notes, createdAt = createdAt
    )

    override fun getAllInvestments(): Flow<List<Investment>> =
        investmentDao.getAllInvestments().map { list -> list.map { it.toDomain() } }

    override fun getInvestmentById(id: Long): Flow<Investment?> =
        investmentDao.getInvestmentById(id).map { it?.toDomain() }

    override suspend fun getInvestmentByIdOnce(id: Long): Investment? =
        investmentDao.getInvestmentByIdOnce(id)?.toDomain()

    override suspend fun insert(investment: Investment): Long =
        investmentDao.insert(investment.toEntity())

    override suspend fun update(investment: Investment) =
        investmentDao.update(investment.toEntity())

    override suspend fun delete(investment: Investment) =
        investmentDao.delete(investment.toEntity())

    override fun getTotalInvested(): Flow<Double?> =
        investmentDao.getTotalInvested()

    override fun getTotalCurrentValue(): Flow<Double?> =
        investmentDao.getTotalCurrentValue()

    override fun getInvestmentsByType(): Flow<List<Pair<String, Double>>> =
        investmentDao.getInvestmentsByType().map { list -> list.map { it.type to it.total } }
}
