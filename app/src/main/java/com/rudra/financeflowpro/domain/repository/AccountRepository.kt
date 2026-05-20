package com.rudra.financeflowpro.domain.repository

import com.rudra.financeflowpro.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    fun getAccountById(id: Long): Flow<Account?>
    suspend fun getAccountByIdOnce(id: Long): Account?
    suspend fun insert(account: Account): Long
    suspend fun update(account: Account)
    suspend fun delete(account: Account)
    suspend fun updateBalance(accountId: Long, amount: Double)
    suspend fun getDefaultAccount(): Account?
    suspend fun setDefaultAccount(accountId: Long)
    fun getTotalNetWorth(): Flow<Double>
    suspend fun getAccountCount(): Int
    suspend fun archiveAccount(id: Long)
    suspend fun getAllAccountsOnce(): List<Account>
}
