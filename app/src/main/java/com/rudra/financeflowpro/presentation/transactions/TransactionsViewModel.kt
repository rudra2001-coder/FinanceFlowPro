package com.rudra.financeflowpro.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Transaction>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) transactionRepository.getAllTransactions()
            else transactionRepository.searchTransactions(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.delete(transaction)
            if (transaction.type == TransactionType.EXPENSE) {
                accountRepository.updateBalance(transaction.accountId, transaction.amount)
            } else if (transaction.type == TransactionType.INCOME) {
                accountRepository.updateBalance(transaction.accountId, -transaction.amount)
            }
        }
    }

    fun deleteBatch(ids: List<Long>) {
        viewModelScope.launch { transactionRepository.deleteBatch(ids) }
    }
}
