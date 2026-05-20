package com.rudra.financeflowpro.presentation.investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.Investment
import com.rudra.financeflowpro.domain.model.InvestmentType
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.InvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditInvestmentViewModel @Inject constructor(
    private val investmentRepository: InvestmentRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _type = MutableStateFlow(InvestmentType.STOCKS)
    val type: StateFlow<InvestmentType> = _type.asStateFlow()

    private val _amountInvested = MutableStateFlow("")
    val amountInvested: StateFlow<String> = _amountInvested.asStateFlow()

    private val _currentValue = MutableStateFlow("")
    val currentValue: StateFlow<String> = _currentValue.asStateFlow()

    private val _units = MutableStateFlow("")
    val units: StateFlow<String> = _units.asStateFlow()

    private val _buyPrice = MutableStateFlow("")
    val buyPrice: StateFlow<String> = _buyPrice.asStateFlow()

    private val _linkedAccountId = MutableStateFlow<Long?>(null)
    val linkedAccountId: StateFlow<Long?> = _linkedAccountId.asStateFlow()

    private val _startDate = MutableStateFlow(System.currentTimeMillis())
    val startDate: StateFlow<Long> = _startDate.asStateFlow()

    private val _maturityDate = MutableStateFlow<Long?>(null)
    val maturityDate: StateFlow<Long?> = _maturityDate.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    var editingInvestmentId: Long? = null

    fun loadInvestment(id: Long) {
        viewModelScope.launch {
            val inv = investmentRepository.getInvestmentByIdOnce(id) ?: return@launch
            _name.value = inv.name
            _type.value = inv.type
            _amountInvested.value = inv.amountInvested.toString()
            _currentValue.value = inv.currentValue.toString()
            _units.value = inv.units?.toString() ?: ""
            _buyPrice.value = inv.buyPrice?.toString() ?: ""
            _linkedAccountId.value = inv.linkedAccountId
            _startDate.value = inv.startDate
            _maturityDate.value = inv.maturityDate
            _notes.value = inv.notes
            editingInvestmentId = id
        }
    }

    fun updateName(v: String) { _name.value = v }
    fun updateType(v: InvestmentType) { _type.value = v }
    fun updateAmountInvested(v: String) { _amountInvested.value = v }
    fun updateCurrentValue(v: String) { _currentValue.value = v }
    fun updateUnits(v: String) { _units.value = v }
    fun updateBuyPrice(v: String) { _buyPrice.value = v }
    fun updateLinkedAccountId(v: Long) { _linkedAccountId.value = v }
    fun updateStartDate(v: Long) { _startDate.value = v }
    fun updateMaturityDate(v: Long?) { _maturityDate.value = v }
    fun updateNotes(v: String) { _notes.value = v }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val now = System.currentTimeMillis()
            val investment = Investment(
                id = editingInvestmentId ?: 0,
                name = _name.value,
                type = _type.value,
                amountInvested = _amountInvested.value.toDoubleOrNull() ?: 0.0,
                currentValue = _currentValue.value.toDoubleOrNull() ?: 0.0,
                units = _units.value.toDoubleOrNull(),
                buyPrice = _buyPrice.value.toDoubleOrNull(),
                linkedAccountId = _linkedAccountId.value,
                startDate = _startDate.value,
                maturityDate = _maturityDate.value,
                notes = _notes.value,
                createdAt = if (editingInvestmentId == null) now else 0
            )
            if (editingInvestmentId != null) investmentRepository.update(investment)
            else investmentRepository.insert(investment)
            _isSaving.value = false
            onSuccess()
        }
    }
}
