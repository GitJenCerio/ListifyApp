package com.jencerio.listifyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jencerio.listifyapp.model.ShoppingReminder
import com.jencerio.listifyapp.repository.ShoppingReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShoppingReminderViewModel(private val repository: ShoppingReminderRepository) : ViewModel() {
    private val _shoppingReminderItems = MutableStateFlow<List<ShoppingReminder>>(emptyList())
    val shoppingReminderItems: StateFlow<List<ShoppingReminder>> = _shoppingReminderItems

    init {
        viewModelScope.launch {
            repository.getShoppingReminders().collectLatest { items ->
                _shoppingReminderItems.value = items
            }
        }
    }

    fun addShoppingReminderItem(shoppingReminder: ShoppingReminder) {
        viewModelScope.launch {
            repository.addShoppingReminder(shoppingReminder)
        }
    }

    fun updateShoppingReminderItem(shoppingReminder: ShoppingReminder) {
        viewModelScope.launch {
            repository.updateShoppingReminder(shoppingReminder)
        }
    }

    fun deleteShoppingReminderItem(shoppingReminder: ShoppingReminder) {
        viewModelScope.launch {
            repository.deleteShoppingReminder(shoppingReminder)
        }
    }

    // Function to trigger manual sync with remote server
    fun syncWithRemoteServer() {
        viewModelScope.launch {
            repository.syncWithRemoteServer()
        }
    }
}

class ShoppingReminderViewModelFactory(
    private val repository: ShoppingReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoppingReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}