package com.jencerio.listifyapp.auth


import com.google.firebase.auth.FirebaseAuth

object AuthHelper {
    fun getFirebaseToken(callback: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result?.token)
            } else {
                callback(null)
            }
        }
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
