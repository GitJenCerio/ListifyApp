package com.jencerio.listifyapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.viewmodel.ShoppingListViewModel
import java.util.UUID
import androidx.compose.ui.platform.LocalContext
import com.jencerio.listifyapp.database.AppDatabase

import com.jencerio.listifyapp.repository.ShoppingListRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jencerio.listifyapp.factory.ShoppingListViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val shoppingListDao = AppDatabase.getDatabase(context).shoppingListDao()
    val repository = ShoppingListRepository(shoppingListDao)
    val viewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(repository))

    val shoppingLists by viewModel.shoppingLists.collectAsState()
    val filteredShoppingLists = shoppingLists.filter { !it.isDeleted }
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ShoppingList?>(null) }
    var shoppingListTitle by remember { mutableStateOf("") }

    val userId = AuthHelper.getCurrentUserId()

    LaunchedEffect(Unit) {
        viewModel.syncShoppingListPendingItems()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Lists") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shoppingListTitle = ""
                editingItem = null
                isDialogOpen = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Shopping List")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(filteredShoppingLists.size) { index ->
                    ShoppingListRow(
                        shoppingList = filteredShoppingLists[index],
                        onEdit = {
                            editingItem = it
                            shoppingListTitle = it.title
                            isDialogOpen = true
                        },
                        onDelete = { shoppingList ->
                            Log.d("ShoppingListScreen", "Soft deleting item: ${shoppingList.id}")
                            viewModel.softDeleteShoppingList(shoppingList)
                        }
                    )
                }
            }
        }

        if (isDialogOpen) {
            ShoppingListDialog(
                onDismiss = { isDialogOpen = false },
                onConfirm = {
                    if (shoppingListTitle.isNotBlank() && userId != null) {
                        if (editingItem != null) {
                            Log.d("ShoppingListScreen", "Updating item: ${editingItem!!.id}")
                            viewModel.updateShoppingList(
                                editingItem!!.copy(
                                    title = shoppingListTitle,
                                    syncStatus = "PENDING",
                                    isSynced = false
                                )
                            )
                        } else {
                            Log.d("ShoppingListScreen", "Adding new item")
                            viewModel.addShoppingList(
                                ShoppingList(
                                    id = UUID.randomUUID().toString(),
                                    userId = userId,
                                    title = shoppingListTitle,
                                    syncStatus = "PENDING",
                                    isSynced = false
                                )
                            )
                        }
                        viewModel.syncShoppingListPendingItems()
                    }
                    isDialogOpen = false
                },
                shoppingListTitle = shoppingListTitle,
                onShoppingListTitleChange = { shoppingListTitle = it }
            )
        }
    }
}



@Composable
fun ShoppingListRow(shoppingList: ShoppingList, onEdit: (ShoppingList) -> Unit, onDelete: (ShoppingList) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(shoppingList.title, fontWeight = FontWeight.Bold)
            }
            Row {
                IconButton(onClick = { onEdit(shoppingList) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(shoppingList) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun ShoppingListDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,

    shoppingListTitle: String,
    onShoppingListTitleChange: (String) -> Unit,

) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Shopping List") },
        text = {
            Column {
                OutlinedTextField(
                    value = shoppingListTitle,
                    onValueChange = onShoppingListTitleChange,
                    label = { Text("Shopping List Title") }
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
