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

    fun useEmulatorForTesting() {
        db.useEmulator("10.0.2.2", 8080)  // Emulator host and port for Firestore
    }

    fun testFirestoreConnection() {
        val testDoc = hashMapOf("message" to "Firestore connected successfully!")

        db.collection("users").document("test_document")
            .set(testDoc)
            .addOnSuccessListener {
                Log.d("Firestore", "Connection successful! Data written to users collection.")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Connection failed: ${e.message}")
            }
    }
}
