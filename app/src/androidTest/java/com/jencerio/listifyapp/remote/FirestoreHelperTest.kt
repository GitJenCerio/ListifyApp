package com.jencerio.listifyapp.remote

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FirestoreHelperTest {

    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.initializeApp(context)

        firestore = FirebaseFirestore.getInstance() // 🔹 Initialize Firestore

        val email = "your-email@gmail.com"
        val password = "your-password"

        val latch = CountDownLatch(1)

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("FirestoreTest", "Signed in as: $email")
                latch.countDown()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreTest", "Authentication failed: ${e.message}")
                latch.countDown()
            }

        latch.await(5, TimeUnit.SECONDS) // Wait for authentication before running tests
    }

    @Test
    fun testFirestoreWriteAndRead() {
        val latch = CountDownLatch(1)
        val testDocId = "test_document_123"
        val testData = hashMapOf("message" to "Testing Firestore write and read!")

        // Write test data
        firestore.collection("users").document(testDocId)
            .set(testData)
            .addOnSuccessListener {
                Log.d("FirestoreTest", "Document successfully written!")
                // Now read the data
                firestore.collection("users").document(testDocId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val message = document.getString("message")
                            Log.d("FirestoreTest", "Read message: $message")
                            assertTrue("Data mismatch!", message == testData["message"])
                        } else {
                            Log.e("FirestoreTest", "Document not found!")
                        }
                        latch.countDown()
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreTest", "Read failed: ${e.message}")
                        latch.countDown()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreTest", "Write failed: ${e.message}")
                latch.countDown()
            }

        latch.await(5, TimeUnit.SECONDS) // Wait for async Firestore operation
    }
}
