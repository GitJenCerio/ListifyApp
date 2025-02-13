package com.jencerio.listifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jencerio.listifyapp.model.BudgetCategory
import com.jencerio.listifyapp.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {
    private val _budgetItems = MutableStateFlow<List<BudgetCategory>>(emptyList())
    val budgetItems: StateFlow<List<BudgetCategory>> get() = _budgetItems

    init {
        viewModelScope.launch {
            repository.budgetItems.collect { items ->
                _budgetItems.value = items
            }
        }
    }

    fun addBudgetItem(budgetCategory: BudgetCategory) {
        viewModelScope.launch {
            repository.addBudgetItem(budgetCategory)
        }
    }

    fun updateBudgetItem(budgetCategory: BudgetCategory) {
        viewModelScope.launch {
            repository.updateBudgetItem(budgetCategory)
        }
    }

    fun deleteBudgetItem(budgetCategory: BudgetCategory) {
        viewModelScope.launch {
            repository.deleteBudgetItem(budgetCategory)
        }
    }
}
