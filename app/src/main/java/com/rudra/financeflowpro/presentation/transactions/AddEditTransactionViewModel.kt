package com.rudra.financeflowpro.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.Transaction
import com.rudra.financeflowpro.domain.model.TransactionType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.TransactionRepository
import com.rudra.financeflowpro.domain.usecase.AddTransactionUseCase
import com.rudra.financeflowpro.util.CategoryConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _type = MutableStateFlow(TransactionType.EXPENSE)
    val type: StateFlow<TransactionType> = _type.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _subcategory = MutableStateFlow("")
    val subcategory: StateFlow<String> = _subcategory.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _accountId = MutableStateFlow<Long?>(null)
    val accountId: StateFlow<Long?> = _accountId.asStateFlow()

    private val _date = MutableStateFlow(System.currentTimeMillis())
    val date: StateFlow<Long> = _date.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _isRecurring = MutableStateFlow(false)
    val isRecurring: StateFlow<Boolean> = _isRecurring.asStateFlow()

    private val _recurringInterval = MutableStateFlow("MONTHLY")
    val recurringInterval: StateFlow<String> = _recurringInterval.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    var editingTransactionId: Long? = null

    val categories: List<String>
        get() = if (_type.value == TransactionType.INCOME) CategoryConstants.incomeCategories
        else CategoryConstants.allCategories

    val subcategories: List<String>
        get() = CategoryConstants.getSubcategories(_category.value)

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            val tx = transactionRepository.getTransactionByIdOnce(id) ?: return@launch
            _type.value = tx.type
            _amount.value = tx.amount.toString()
            _category.value = tx.category
            _subcategory.value = tx.subcategory
            _description.value = tx.description
            _accountId.value = tx.accountId
            _date.value = tx.date
            _note.value = tx.note
            _isRecurring.value = tx.isRecurring
            _recurringInterval.value = tx.recurringInterval ?: "MONTHLY"
            editingTransactionId = id
        }
    }

    fun updateType(value: TransactionType) { _type.value = value; _category.value = "" }
    fun updateAmount(value: String) { _amount.value = value }
    fun updateCategory(value: String) { _category.value = value; _subcategory.value = "" }
    fun updateSubcategory(value: String) { _subcategory.value = value }
    fun updateDescription(value: String) { _description.value = value }
    fun updateAccountId(value: Long) { _accountId.value = value }
    fun updateDate(value: Long) { _date.value = value }
    fun updateNote(value: String) { _note.value = value }
    fun updateIsRecurring(value: Boolean) { _isRecurring.value = value }
    fun updateRecurringInterval(value: String) { _recurringInterval.value = value }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val now = System.currentTimeMillis()
            val amountValue = _amount.value.toDoubleOrNull() ?: 0.0
            val transaction = Transaction(
                id = editingTransactionId ?: 0,
                accountId = _accountId.value ?: return@launch,
                type = _type.value,
                amount = amountValue,
                category = _category.value,
                subcategory = _subcategory.value,
                description = _description.value,
                date = _date.value,
                time = _date.value,
                note = _note.value,
                isRecurring = _isRecurring.value,
                recurringInterval = if (_isRecurring.value) _recurringInterval.value else null,
                createdAt = if (editingTransactionId == null) now else 0
            )

            if (editingTransactionId != null) {
                transactionRepository.update(transaction)
            } else {
                addTransactionUseCase(transaction)
            }
            _isSaving.value = false
            onSuccess()
        }
    }
}
