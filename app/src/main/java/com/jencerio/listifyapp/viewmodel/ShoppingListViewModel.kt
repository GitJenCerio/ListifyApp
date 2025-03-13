//package com.jencerio.listifyapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.jencerio.listifyapp.auth.AuthHelper
//import com.jencerio.listifyapp.model.ShoppingList
//import com.jencerio.listifyapp.repository.ShoppingListRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class ShoppingItemViewModel(private val repository: ShoppingListRepository) : ViewModel() {
//    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(emptyList())
//    val shoppingLists: StateFlow<List<ShoppingList>> get() = _shoppingLists
//
//    init {
//        viewModelScope.launch {
//            repository.getShoppingLists().collect { items ->
//                _shoppingLists.value = items
//            }
//        }
//    }
//
//    fun addShoppingItem(title: String, isFavorite: Boolean) {
//        AuthHelper.getFirebaseToken { token ->
//            if (token != null) {
//                val userId = AuthHelper.getUserId() // Ensure this function exists in AuthHelper
//
//                val shoppingItem = ShoppingList(
//                    id = System.currentTimeMillis().toString(), // Unique ID
//                    userId = userId,
//                    title = title,
//                    createdAt = java.util.Date(),
//                    updatedAt = java.util.Date(),
//                    isFavorite = isFavorite
//                )
//
//                viewModelScope.launch {
//                    repository.addShoppingList(shoppingItem) // Add to Room
//                    repository.addShoppingListToFirestore(shoppingItem) // Add to Firestore
//                }
//            }
//        }
//    }
//
//    fun updateShoppingItem(shoppingItem: ShoppingList) {
//        viewModelScope.launch {
//            repository.updateShoppingList(shoppingItem) // Update in Room
//            repository.updateShoppingListInFirestore(shoppingItem) // Update in Firestore
//        }
//    }
//
//    fun deleteShoppingItem(shoppingItem: ShoppingList) {
//        viewModelScope.launch {
//            repository.deleteShoppingList(shoppingItem) // Delete from Room
//            repository.deleteShoppingListFromFirestore(shoppingItem) // Delete from Firestore
//        }
//    }
//}
