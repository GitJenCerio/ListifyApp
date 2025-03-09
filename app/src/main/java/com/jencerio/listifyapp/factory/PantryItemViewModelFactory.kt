package com.jencerio.listifyapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jencerio.listifyapp.repository.PantryItemRepository
import com.jencerio.listifyapp.viewmodel.PantryItemViewModel

class PantryItemViewModelFactory(private val repository: PantryItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PantryItemViewModelFactory::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PantryItemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
