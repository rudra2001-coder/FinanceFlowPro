package com.rudra.financeflowpro.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.financeflowpro.domain.model.Budget
import com.rudra.financeflowpro.domain.repository.BudgetRepository
import com.rudra.financeflowpro.util.CategoryConstants
import com.rudra.financeflowpro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val categories = CategoryConstants.allCategories

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun updateCategory(v: String) { _category.value = v }
    fun updateAmount(v: String) { _amount.value = v }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            val budget = Budget(
                category = _category.value,
                amount = _amount.value.toDoubleOrNull() ?: 0.0,
                month = DateUtils.getCurrentYearMonth(),
                createdAt = System.currentTimeMillis()
            )
            budgetRepository.insert(budget)
            _isSaving.value = false
            onSuccess()
        }
    }
}
