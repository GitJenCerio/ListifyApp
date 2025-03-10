package com.jencerio.listifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jencerio.listifyapp.model.ShoppingItem
import com.jencerio.listifyapp.repository.ShoppingItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingItemViewModel(private val repository: ShoppingItemRepository) : ViewModel() {
    private val _shoppingItems = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val pantryItems: StateFlow<List<ShoppingItem>> get() = pantryItems

    init {
        viewModelScope.launch {
            repository.shoppingItems.collect { items ->
                _shoppingItems.value = items
            }
        }
    }

    fun addShoppingItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            repository.addShoppingItem(shoppingItem)
        }
    }

    fun updateShoppingItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(shoppingItem)
        }
    }

    fun deleteShoppingItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(shoppingItem)
        }
    }
}
