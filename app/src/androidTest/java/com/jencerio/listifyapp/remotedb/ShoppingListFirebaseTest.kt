//package com.jencerio.listifyapp.remotedb
//
//import android.util.Log
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.jencerio.listifyapp.model.ShoppingList
//import com.jencerio.listifyapp.repository.ShoppingListRepository
//import junit.framework.TestCase.assertTrue
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.util.Date
//import java.util.UUID
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//
//@RunWith(AndroidJUnit4::class)
//class FirestoreShoppingListTest {
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//    private lateinit var repository: ShoppingListRepository
//
//    @Before
//    fun setup() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        FirebaseApp.initializeApp(context)
//
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//        repository = ShoppingListRepository(shoppingListDao = null)
//
//        val email = "your-email"
//        val password = "your-password"
//
//        val latch = CountDownLatch(1)
//
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                Log.d("FirestoreTest", "Authenticated as: $email")
//                latch.countDown()
//            }
//            .addOnFailureListener { e ->
//                Log.e("FirestoreTest", "Auth failed: ${e.message}")
//                latch.countDown()
//            }
//
//        latch.await(5, TimeUnit.SECONDS) // Wait for authentication
//    }
//
//    @Test
//    fun testInsertShoppingList() = runBlocking<Unit> {
//        val latch = CountDownLatch(1)
//
//        val userId = auth.currentUser?.uid ?: "unknown_user"
//        val shoppingList = ShoppingList(
//            id = UUID.randomUUID().toString(),
//            userId = userId,
//            title = "Test Shopping List",
//            createdAt = Date(),
//            updatedAt = Date(),
//            isFavorite = false
//        )
//
//        repository.addShoppingListToFirestore(shoppingList)
//        Log.d("FirestoreTest", "ShoppingList inserted via Repository: ${shoppingList.id}")
//
//        val retrievedList = repository.getShoppingListFromFirestore(shoppingList.id)
//        if (retrievedList != null) {
//            Log.d("FirestoreTest", "Retrieved title: ${retrievedList.title}")
//            assertTrue("Title mismatch!", retrievedList.title == shoppingList.title)
//        } else {
//            Log.e("FirestoreTest", "Document not found!")
//        }
//
//        latch.countDown()
//        latch.await(5, TimeUnit.SECONDS) // Wait for Firestore operation
//    }
//}
