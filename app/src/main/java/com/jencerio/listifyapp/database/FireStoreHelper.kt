package com.jencerio.listifyapp.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import android.util.Log

object FirestoreHelper {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // Enables offline support
                .build()
        }
    }

    fun getFirestoreInstance(): FirebaseFirestore {
        return db
    }

    // Test Firestore connection by adding dummy data
    fun testFirestoreConnection() {
        val testDoc = hashMapOf("message" to "Firestore connected successfully!")

        db.collection("test_collection").document("test_document")
            .set(testDoc)
            .addOnSuccessListener {
                Log.d("Firestore", "Connection successful! Data written.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Connection failed: ${e.message}")
            }
    }
}
