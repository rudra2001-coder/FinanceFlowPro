package com.rudra.financeflowpro.data.repository

import com.rudra.financeflowpro.data.local.AccountDao
import com.rudra.financeflowpro.data.local.AccountEntity
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.AccountType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    private fun AccountEntity.toDomain() = Account(
        id = id, name = name, type = AccountType.valueOf(type),
        balance = balance, currency = currency, color = color,
        icon = icon, isDefault = isDefault, isArchived = isArchived,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Account.toEntity() = AccountEntity(
        id = id, name = name, type = type.name,
        balance = balance, currency = currency, color = color,
        icon = icon, isDefault = isDefault, isArchived = isArchived,
        createdAt = createdAt, updatedAt = updatedAt
    )

    override fun getAllAccounts(): Flow<List<Account>> =
        accountDao.getAllAccounts().map { list -> list.map { it.toDomain() } }

    override fun getAccountById(id: Long): Flow<Account?> =
        accountDao.getAccountById(id).map { it?.toDomain() }

    override suspend fun getAccountByIdOnce(id: Long): Account? =
        accountDao.getAccountByIdOnce(id)?.toDomain()

    override suspend fun insert(account: Account): Long =
        accountDao.insert(account.toEntity())

    override suspend fun update(account: Account) =
        accountDao.update(account.toEntity())

    override suspend fun delete(account: Account) =
        accountDao.delete(account.toEntity())

    override suspend fun updateBalance(accountId: Long, amount: Double) =
        accountDao.updateBalance(accountId, amount)

    override suspend fun getDefaultAccount(): Account? =
        accountDao.getDefaultAccount()?.toDomain()

    override suspend fun setDefaultAccount(accountId: Long) {
        accountDao.clearDefaultAccount()
        val account = accountDao.getAccountByIdOnce(accountId) ?: return
        accountDao.update(account.copy(isDefault = true))
    }

    override fun getTotalNetWorth(): Flow<Double> =
        accountDao.getTotalNetWorth().map { it ?: 0.0 }

    override suspend fun getAccountCount(): Int =
        accountDao.getAccountCount()

    override suspend fun archiveAccount(id: Long) =
        accountDao.archiveAccount(id)

    override suspend fun getAllAccountsOnce(): List<Account> =
        accountDao.getAllAccountsOnce().map { it.toDomain() }
}
