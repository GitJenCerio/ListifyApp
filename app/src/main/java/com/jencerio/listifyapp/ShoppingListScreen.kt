package com.jencerio.listifyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.jencerio.listifyapp.auth.AuthHelper
import com.jencerio.listifyapp.model.ShoppingList
import com.jencerio.listifyapp.ui.theme.GreenSecondary
import com.jencerio.listifyapp.viewmodel.ShoppingListViewModel
import java.util.Date
import java.util.UUID
import androidx.compose.ui.platform.LocalContext
import com.jencerio.listifyapp.database.AppDatabase

import com.jencerio.listifyapp.repository.ShoppingListRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jencerio.listifyapp.factory.ShoppingListViewModelFactory
import com.jencerio.listifyapp.model.Budget


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val shoppingListDao = AppDatabase.getDatabase(context).shoppingListDao()
    val shoppingListRepository = ShoppingListRepository(shoppingListDao)
    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModelFactory(shoppingListRepository))

    val shoppingLists by shoppingListViewModel.shoppingLists.collectAsState()
    var isDialogOpen by remember { mutableStateOf(false) }

    var shoppingListTitle by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }
    var categoryAmount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Budget?>(null) }


    var showDialog by remember { mutableStateOf(false) }
    var selectedShoppingList by remember { mutableStateOf<ShoppingList?>(null) }
    var showAddListDialog by remember { mutableStateOf(false) } // State for Add List dialog

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Shopping Lists",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(GreenSecondary),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to AddNewListScreen
                    navController.navigate("new_list")
                },
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add Shopping List")
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                if (shoppingListViewModel.shoppingLists.isEmpty()) {
                    Text(
                        text = "No shopping lists created yet.",
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(shoppingListViewModel.shoppingLists) { shoppingList ->
                            ShoppingListItem(
                                shoppingList = shoppingList,
                                onEdit = {
                                    selectedShoppingList = shoppingList
                                    showDialog = true
                                },
                                onDelete = {
                                    shoppingListViewModel.removeShoppingList(shoppingList)
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    // Dialog for adding a new shopping list
    if (showAddListDialog) {
        AddShoppingListDialog(
            onDismiss = { showAddListDialog = false },
            onSave = { newName ->
                shoppingListViewModel.addShoppingList(
                    newName,
                    emptyList()
                ) // Empty list for new shopping list
                showAddListDialog = false
            }
        )
    }
    // Dialog for editing shopping list
    if (showDialog && selectedShoppingList != null) {
        EditShoppingListDialog(
            shoppingList = selectedShoppingList!!,
            onDismiss = { showDialog = false },
            onSave = { updatedName, updatedItems ->
                shoppingListViewModel.updateShoppingList(
                    selectedShoppingList!!,
                    updatedName,
                    updatedItems
                )
                showDialog = false
            }
        )
    }
}


// Dialog for adding a new shopping list
@Composable
fun AddShoppingListDialog(
    onDismiss: () -> Unit,
    onSave: (ShoppingList) -> Unit
) {
    var listTitle by remember { mutableStateOf("") }
    var showAuthError by remember { mutableStateOf(false) }
    val userId = remember { AuthHelper.getCurrentUserId() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create New List",
                    style = MaterialTheme.typography.headlineSmall,
                    color = GreenSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = listTitle,
                    onValueChange = { listTitle = it },
                    label = { Text("List Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    isError = showAuthError
                )

                if (showAuthError) {
                    Text(
                        text = "Authentication required!",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            when {
                                listTitle.isBlank() -> {
                                    // Handle empty title
                                }

                                userId == null -> {
                                    showAuthError = true
                                }

                                else -> {
                                    val newList = ShoppingList(
                                        id = UUID.randomUUID().toString(),
                                        userId = userId,
                                        title = listTitle,
                                        createdAt = Date(),
                                        updatedAt = Date()
                                    )
                                    onSave(newList)
                                    onDismiss()
                                }
                            }
                        },
                        enabled = listTitle.isNotBlank()
                    ) {
                        Text("Create List")
                    }
                }
            }
        }
    }
}


@Composable
fun EditShoppingListDialog(
    shoppingList: ShoppingList,
    onDismiss: () -> Unit,
    onSave: (String, List<ShoppingItem>) -> Unit
) {
    var listName by remember { mutableStateOf(shoppingList.name) }
    var listItems by remember {
        mutableStateOf(shoppingList.items.map { it.copy() }.toMutableList())
    }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("") }
    var newItemUnit by remember { mutableStateOf(units.first()) }
    var newItemUnitExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(min = 600.dp, max = 700.dp), // Adjust dialog height
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Shopping List",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Editable items
                LazyColumn {
                    itemsIndexed(listItems) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = item.name,
                                modifier = Modifier.weight(1f),
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )

                            OutlinedTextField(
                                value = item.quantity.takeIf { it > 0 }?.toString() ?: "",
                                onValueChange = { newQuantity ->
                                    val parsedQuantity = newQuantity.toIntOrNull() ?: 0
                                    listItems[index] = item.copy(quantity = parsedQuantity)
                                },
                                label = { Text("Qty") },
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(end = 8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Box(modifier = Modifier.width(120.dp)) {
                                OutlinedTextField(
                                    value = item.unit,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Unit") },
                                    trailingIcon = {
                                        IconButton(onClick = { /* Handle unit selection */ }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                                                contentDescription = "Select unit"
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add new item section
                Text(
                    text = "Add New Item",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = newItemQuantity,
                    onValueChange = { newItemQuantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)) {
                    OutlinedTextField(
                        value = newItemUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = {
                            IconButton(onClick = { newItemUnitExpanded = !newItemUnitExpanded }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                                    contentDescription = "Select unit"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = newItemUnitExpanded,
                        onDismissRequest = { newItemUnitExpanded = false }
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    newItemUnit = unit
                                    newItemUnitExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (newItemName.isNotBlank() && newItemQuantity.toIntOrNull() != null) {
                            listItems.add(
                                ShoppingItem(
                                    name = newItemName,
                                    quantity = newItemQuantity.toInt(),
                                    unit = newItemUnit
                                )
                            )
                            newItemName = ""
                            newItemQuantity = ""
                            newItemUnit = units.first()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Item")
                }

                // Save and Cancel buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { onSave(listName, listItems) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
