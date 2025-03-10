package com.jencerio.listifyapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {
    private val _shoppingListItems = MutableStateFlow<List<ShoppingList>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingList>> get() = _shoppingListItems

    init {
        viewModelScope.launch {
            repository.shoppingLists.collect { items ->
                _shoppingListItems.value = items
            }
        }
    }

    fun addShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.addShoppingList(shoppingList)
        }
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.updateShoppingList(shoppingList)
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.deleteShoppingList(shoppingList)
        }
    }
}
