
# Homework 8: Sprint 3: Local Database Management Implementation

## Overview
In this sprint, our main focus is on creating test cases for the local database implementation. The objective is to ensure that our approach functions correctly. For instance, when a new shopping list is added to the app, it should be properly stored in the database. Additionally, the data should remain intact even after the app is closed.

While we have successfully implemented the database setup, CRUD operations, and data persistence, we have yet to complete the UI integration. This sprint will focus on rigorous testing to validate our implementation before integrating it into the mobile app's interface.

### Duration: Weeks 5 and 6 | Treated as a Continuous Sprint
### Focus: Implementing database integration and CRUD operations tailored to our mobile app named Listify.


## Tasks

### Set Up Room Database
- Add the necessary Room dependencies to your project.
- Define Entity classes that represent your app's data model.
- Define DAO (Data Access Object) interfaces to handle CRUD operations.
- Set up the RoomDatabase class to manage the database.

### Implement CRUD Operations
- **Create (Insert)**: Implement functionality to add new records (e.g., shopping lists) to the database from user input.
- **Read (Query)**: Query data from the database and display it in the app.
- **Update**: Allow users to modify existing records in the database and save the updated data.
- **Delete**: Implement the ability to remove records from the database.

### Integrate with LiveData
- Use LiveData to observe changes in the database and automatically update the UI when new data is added, updated, or deleted.

## Testing
To ensure our mobile app functions as intended, we are prioritizing testing before UI integration. The following mandatory tests must be performed and documented in our test case document.

### Essential Tests
#### Functional Testing (CRUD Operations)
- Test creating, reading, updating, and deleting data in the local database (Room) to ensure all database operations function correctly.

#### Integration Testing (Database Logic)
- Validate that the DAO methods interact correctly with the database.
- Ensure Flow-based queries provide real-time updates.

#### Regression Testing
- After implementing Room and CRUD operations, check if any previously working features (e.g., database synchronization, data handling) were negatively affected by the new code.

## Deliverables for Sprint 3 (Due at the End of Week 6)

### Code Submission
- Link to the updated code repository with:
    - Implemented database integration and CRUD operations.
    - Unit tests for CRUD operations and database logic.

### Documentation
- A brief document summarizing:
    - Database integration and CRUD functionality implemented during Sprint 3.
    - Challenges encountered and how they were addressed.
    - Refinements made based on testing and validation.

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

### DAO Implementation
```kotlin
package com.jencerio.listifyapp.dao

import androidx.room.*
import com.jencerio.listifyapp.model.ShoppingList
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists")
    fun getAll(): Flow<List<ShoppingList>> // Flow for real-time updates

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingList)

    @Update
    suspend fun update(shoppingList: ShoppingList)

    @Delete
    suspend fun delete(shoppingList: ShoppingList)
}
```
