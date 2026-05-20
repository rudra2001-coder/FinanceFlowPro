package com.rudra.financeflowpro.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY startDate DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE id = :id")
    fun getInvestmentById(id: Long): Flow<InvestmentEntity?>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getInvestmentByIdOnce(id: Long): InvestmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investment: InvestmentEntity): Long

    @Update
    suspend fun update(investment: InvestmentEntity)

    @Delete
    suspend fun delete(investment: InvestmentEntity)

    @Query("SELECT SUM(amountInvested) FROM investments")
    fun getTotalInvested(): Flow<Double?>

    @Query("SELECT SUM(currentValue) FROM investments")
    fun getTotalCurrentValue(): Flow<Double?>

    @Query("SELECT type, SUM(amountInvested) as total FROM investments GROUP BY type")
    fun getInvestmentsByType(): Flow<List<InvestmentTypeTotal>>
}

data class InvestmentTypeTotal(
    val type: String,
    val total: Double
)
