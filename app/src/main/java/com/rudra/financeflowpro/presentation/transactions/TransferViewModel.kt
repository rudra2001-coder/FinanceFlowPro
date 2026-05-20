package com.rudra.financeflowpro.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.usecase.TransferBetweenAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transferUseCase: TransferBetweenAccountsUseCase
) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _fromAccountId = MutableStateFlow<Long?>(null)
    val fromAccountId: StateFlow<Long?> = _fromAccountId.asStateFlow()

    private val _toAccountId = MutableStateFlow<Long?>(null)
    val toAccountId: StateFlow<Long?> = _toAccountId.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date: StateFlow<Long> = _date.asStateFlow()

    private val _isTransferring = MutableStateFlow(false)
    val isTransferring: StateFlow<Boolean> = _isTransferring.asStateFlow()

    fun updateFromAccount(id: Long) { _fromAccountId.value = id }
    fun updateToAccount(id: Long) { _toAccountId.value = id }
    fun updateAmount(value: String) { _amount.value = value }
    fun updateNote(value: String) { _note.value = value }
    fun updateDate(value: Long) { _date.value = value }

    fun transfer(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isTransferring.value = true
            val amountValue = _amount.value.toDoubleOrNull() ?: return@launch
            transferUseCase(
                fromAccountId = _fromAccountId.value ?: return@launch,
                toAccountId = _toAccountId.value ?: return@launch,
                amount = amountValue,
                date = _date.value,
                note = _note.value
            )
            _isTransferring.value = false
            onSuccess()
        }
    }
}
