package com.rudra.financeflowpro.presentation.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Account
import com.rudra.financeflowpro.domain.model.SavingsGoal
import com.rudra.financeflowpro.domain.repository.AccountRepository
import com.rudra.financeflowpro.domain.repository.SavingsGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditSavingsGoalViewModel @Inject constructor(
    private val savingsGoalRepository: SavingsGoalRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    val accounts: StateFlow<List<Account>> = accountRepository.getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _targetAmount = MutableStateFlow("")
    val targetAmount: StateFlow<String> = _targetAmount.asStateFlow()

    private val _currentAmount = MutableStateFlow("0")
    val currentAmount: StateFlow<String> = _currentAmount.asStateFlow()

    private val _deadline = MutableStateFlow<Long?>(null)
    val deadline: StateFlow<Long?> = _deadline.asStateFlow()

    private val _linkedAccountId = MutableStateFlow<Long?>(null)
    val linkedAccountId: StateFlow<Long?> = _linkedAccountId.asStateFlow()

    private val _color = MutableStateFlow("2196F3")
    val color: StateFlow<String> = _color.asStateFlow()

    private val _icon = MutableStateFlow("savings")
    val icon: StateFlow<String> = _icon.asStateFlow()

    private val _autoContributeAmount = MutableStateFlow("")
    val autoContributeAmount: StateFlow<String> = _autoContributeAmount.asStateFlow()

    private val _autoContributeInterval = MutableStateFlow("MONTHLY")
    val autoContributeInterval: StateFlow<String> = _autoContributeInterval.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    var editingGoalId: Long? = null

    fun loadGoal(id: Long) {
        viewModelScope.launch {
            val goal = savingsGoalRepository.getGoalByIdOnce(id) ?: return@launch
            _name.value = goal.name
            _targetAmount.value = goal.targetAmount.toString()
            _currentAmount.value = goal.currentAmount.toString()
            _deadline.value = goal.deadline
            _linkedAccountId.value = goal.linkedAccountId
            _color.value = goal.color
            _icon.value = goal.icon
            _autoContributeAmount.value = goal.autoContributeAmount?.toString() ?: ""
            _autoContributeInterval.value = goal.autoContributeInterval ?: "MONTHLY"
            editingGoalId = id
        }
    }

    fun updateName(v: String) { _name.value = v }
    fun updateTargetAmount(v: String) { _targetAmount.value = v }
    fun updateCurrentAmount(v: String) { _currentAmount.value = v }
    fun updateDeadline(v: Long?) { _deadline.value = v }
    fun updateLinkedAccountId(v: Long) { _linkedAccountId.value = v }
    fun updateColor(v: String) { _color.value = v }
    fun updateAutoContributeAmount(v: String) { _autoContributeAmount.value = v }
    fun updateAutoContributeInterval(v: String) { _autoContributeInterval.value = v }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val now = System.currentTimeMillis()
            val goal = SavingsGoal(
                id = editingGoalId ?: 0,
                name = _name.value,
                targetAmount = _targetAmount.value.toDoubleOrNull() ?: 0.0,
                currentAmount = _currentAmount.value.toDoubleOrNull() ?: 0.0,
                deadline = _deadline.value,
                linkedAccountId = _linkedAccountId.value,
                color = _color.value,
                icon = _icon.value,
                autoContributeAmount = _autoContributeAmount.value.toDoubleOrNull(),
                autoContributeInterval = if (_autoContributeAmount.value.isNotBlank()) _autoContributeInterval.value else null,
                createdAt = if (editingGoalId == null) now else 0
            )
            if (editingGoalId != null) savingsGoalRepository.update(goal)
            else savingsGoalRepository.insert(goal)
            _isSaving.value = false
            onSuccess()
        }
    }
}
