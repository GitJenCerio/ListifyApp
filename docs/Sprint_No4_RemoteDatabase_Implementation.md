
# Homework 8: Sprint 4: Remote Database Management Implementation

## Overview
In this sprint, our focus is on creating test cases for the remote database implementation using Firebase Firestore. The objective is to ensure that data is correctly stored and persists even after the app is closed. For instance, when a new shopping list is added, it should be properly stored in the Firestore collection named "shopping_lists."

### Duration: Weeks 7 to 9 | Treated as a Continuous Sprint
### Focus: Implementing Firebase integration and CRUD operations for our mobile app, Listify.

## Using Your Sprint Planning and Backlog Document
Refer to your Sprint Planning and Backlog document throughout this sprint to:

- **Track Progress**: Monitor tasks in the Sprint Backlog and ensure timely completion.
- **Guide Development**: Stay focused on the Sprint Goal of efficient data management.
- **Ensure Quality**: Verify that each task meets the Definition of Done (DoD).

## Tasks

### Set Up Firebase
- Add the necessary Firebase Firestore dependencies to the project.
- Define Entity classes that represent the app's data model.
- Implement Firebase methods to handle CRUD operations.
- Research and implement data synchronization between local and remote databases.

### Implement CRUD Operations
- **Create (Insert)**: Add new records (e.g., shopping lists) to Firestore collections.
- **Read (Query)**: Fetch data from Firestore collections and display it in the app.
- **Update**: Modify existing records in Firestore and save the changes.
- **Delete**: Remove records from Firestore collections.

### Integrate with LiveData
- Use LiveData to observe Firestore data changes and update the UI automatically.

## Testing
To ensure the mobile app functions correctly, rigorous testing is required before UI integration. The following tests must be performed and documented.

### Essential Tests
#### Functional Testing (CRUD Operations)
- Validate creating, reading, updating, and deleting data in Firestore.

#### Integration Testing (Database Logic)
- Ensure Firebase Firestore methods interact correctly with the database.
- Verify real-time updates using Flow-based queries.

#### Regression Testing
- Check if previously working features (e.g., data synchronization) are affected by the new code.

## Deliverables for Sprint 4 (Due at the End of Week 9)

### Code Submission
- Link to the updated code repository with:
    - Firebase Firestore integration and CRUD operations.
    - Unit tests for Firestore database logic.

### Documentation
- A brief document summarizing:
    - Firebase integration and CRUD functionality.
    - Challenges encountered and solutions applied.
    - Refinements based on testing and validation.

## Sample Model Implementation

### ShoppingList Model
```kotlin  
package com.jencerio.listifyapp.model  
  
import androidx.room.Entity  
import androidx.room.PrimaryKey  
import androidx.room.TypeConverters  
import com.jencerio.listifyapp.utils.Converters  
import java.util.Date  
  
  
@Entity(tableName = "shopping_lists")  
@TypeConverters(Converters::class)  
data class ShoppingList(  
    @PrimaryKey val id: String = "",  
    val userId: String = "",  
    val title: String = "",  
    val createdAt: Date = Date(),  
    val updatedAt: Date = Date(),  
    val isFavorite: Boolean = false  
) {  
    constructor() : this("", "", "", Date(), Date(), false)  
}
```  

### Firestore Repository Implementation
```kotlin
package com.jencerio.listifyapp.repository  
  
import com.google.firebase.firestore.FirebaseFirestore  
import com.google.firebase.firestore.DocumentSnapshot  
import com.jencerio.listifyapp.dao.ShoppingListDao  
import com.jencerio.listifyapp.model.ShoppingList  
import kotlinx.coroutines.flow.Flow  
import kotlinx.coroutines.tasks.await  
  
class ShoppingListRepository(private val shoppingListDao: ShoppingListDao?) { // 🔹 Make it optional  private val firestore = FirebaseFirestore.getInstance()  
    private val collectionRef = firestore.collection("shopping_lists")  
  
    fun getShoppingLists(): Flow<List<ShoppingList>> {  
        return shoppingListDao?.getAll() ?: throw IllegalStateException("Local DB not initialized")  
    }  
  
    suspend fun addShoppingList(shoppingList: ShoppingList) {  
        shoppingListDao?.insert(shoppingList) ?: throw IllegalStateException("Local DB not initialized")  
    }  
  
    suspend fun updateShoppingList(shoppingList: ShoppingList) {  
        shoppingListDao?.update(shoppingList) ?: throw IllegalStateException("Local DB not initialized")  
    }  
  
    suspend fun deleteShoppingList(shoppingList: ShoppingList) {  
        shoppingListDao?.delete(shoppingList) ?: throw IllegalStateException("Local DB not initialized")  
    }  
  
    // 🔥 Firebase Firestore CRUD Methods 🔥  
  
  suspend fun addShoppingListToFirestore(shoppingList: ShoppingList) {  
        collectionRef.document(shoppingList.id).set(shoppingList).await()  
    }  
  
    suspend fun updateShoppingListInFirestore(shoppingList: ShoppingList) {  
        collectionRef.document(shoppingList.id).set(shoppingList).await()  
    }  
  
    suspend fun deleteShoppingListFromFirestore(shoppingList: ShoppingList) {  
        collectionRef.document(shoppingList.id).delete().await()  
    }  
  
    suspend fun getShoppingListFromFirestore(id: String): ShoppingList? {  
        val documentSnapshot = collectionRef.document(id).get().await()  
        return documentSnapshot.toObject(ShoppingList::class.java)   
    }  
}
```