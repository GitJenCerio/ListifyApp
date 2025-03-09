package com.jencerio.listifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingItemViewModel(private val repository: ShoppingItem) : ViewModel() {
    private val _pantryItems = MutableStateFlow<List<PantryItem>>(emptyList())
    val pantryItems: StateFlow<List<PantryItem>> get() = _pantryItems

    init {
        viewModelScope.launch {
            repository.pantryItems.collect { items ->
                _pantryItems.value = items
            }
        }
    }

    fun addPantryItem(pantryItem: PantryItem) {
        viewModelScope.launch {
            repository.addPantryItem(pantryItem)
        }
    }

    fun updatePantryItem(pantryItem: PantryItem) {
        viewModelScope.launch {
            repository.updatePantryItem(pantryItem)
        }
    }

    fun deletePantryItem(pantryItem: PantryItem) {
        viewModelScope.launch {
            repository.deletePantryItem(pantryItem)
        }
    }
}
