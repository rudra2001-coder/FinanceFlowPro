package com.rudra.financeflowpro.presentation.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.AccountType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _type = MutableStateFlow(AccountType.BANK)
    val type: StateFlow<AccountType> = _type.asStateFlow()

    private val _balance = MutableStateFlow("")
    val balance: StateFlow<String> = _balance.asStateFlow()

    private val _currency = MutableStateFlow("USD")
    val currency: StateFlow<String> = _currency.asStateFlow()

    private val _color = MutableStateFlow("4CAF50")
    val color: StateFlow<String> = _color.asStateFlow()

    private val _icon = MutableStateFlow("account_balance")
    val icon: StateFlow<String> = _icon.asStateFlow()

    private val _isDefault = MutableStateFlow(false)
    val isDefault: StateFlow<Boolean> = _isDefault.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    var editingAccountId: Long? = null

    fun loadAccount(id: Long) {
        viewModelScope.launch {
            val account = accountRepository.getAccountByIdOnce(id) ?: return@launch
            _name.value = account.name
            _type.value = account.type
            _balance.value = account.balance.toString()
            _currency.value = account.currency
            _color.value = account.color
            _icon.value = account.icon
            _isDefault.value = account.isDefault
            editingAccountId = id
        }
    }

    fun updateName(value: String) { _name.value = value }
    fun updateType(value: AccountType) { _type.value = value }
    fun updateBalance(value: String) { _balance.value = value }
    fun updateCurrency(value: String) { _currency.value = value }
    fun updateColor(value: String) { _color.value = value }
    fun updateIcon(value: String) { _icon.value = value }
    fun updateIsDefault(value: Boolean) { _isDefault.value = value }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val now = System.currentTimeMillis()
            val account = Account(
                id = editingAccountId ?: 0,
                name = _name.value,
                type = _type.value,
                balance = _balance.value.toDoubleOrNull() ?: 0.0,
                currency = _currency.value,
                color = _color.value,
                icon = _icon.value,
                isDefault = _isDefault.value,
                createdAt = if (editingAccountId == null) now else 0,
                updatedAt = now
            )
            if (editingAccountId != null) {
                accountRepository.update(account)
            } else {
                accountRepository.insert(account)
            }
            if (_isDefault.value) {
                accountRepository.setDefaultAccount(editingAccountId ?: return@launch)
            }
            _isSaving.value = false
            onSuccess()
        }
    }
}
