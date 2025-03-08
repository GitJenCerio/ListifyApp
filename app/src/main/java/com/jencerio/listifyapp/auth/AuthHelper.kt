package com.jencerio.listifyapp.auth


import com.google.firebase.auth.FirebaseAuth

object AuthHelper {
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}
