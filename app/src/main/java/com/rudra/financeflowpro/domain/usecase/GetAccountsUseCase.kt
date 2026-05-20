package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.getAllAccounts()
}
