package com.jencerio.listifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jencerio.listifyapp.model.Budget
import com.jencerio.listifyapp.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {
    private val _budgetItems = MutableStateFlow<List<Budget>>(emptyList())
    val budgetItems: StateFlow<List<Budget>> get() = _budgetItems

    init {
        viewModelScope.launch {
            repository.budgetItems.collect { items ->
                _budgetItems.value = items
            }
        }
    }

    fun addBudgetItem(budget: Budget) {
        viewModelScope.launch {
            repository.addBudgetItem(budget)
        }
    }

    fun updateBudgetItem(budget: Budget) {
        viewModelScope.launch {
            repository.updateBudgetItem(budget)
        }
    }

    fun deleteBudgetItem(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudgetItem(budget)
        }
    }
}
