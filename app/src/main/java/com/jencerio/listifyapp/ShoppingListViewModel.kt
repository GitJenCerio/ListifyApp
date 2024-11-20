package com.jencerio.listifyapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class ShoppingItem(
    val name: String,
    val quantity: Int,
    val unit: String
)

data class ShoppingList(val name: String, val items: List<ShoppingItem>)

class ShoppingListViewModel : ViewModel() {
    val shoppingLists = mutableStateListOf<ShoppingList>()

    fun addShoppingList(name: String, items: List<ShoppingItem>) {
        shoppingLists.add(ShoppingList(name, items))
    }

    fun updateShoppingList(oldList: ShoppingList, newName: String, newItems: List<ShoppingItem>) {
        val index = shoppingLists.indexOf(oldList)
        if (index != -1) {
            shoppingLists[index] = ShoppingList(newName, newItems)
        }
    }

    fun removeShoppingList(list: ShoppingList) {
        shoppingLists.remove(list)
    }
}

