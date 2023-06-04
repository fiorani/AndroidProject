package com.example.eatit.data

import androidx.annotation.WorkerThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.eatit.EatItApp
import com.example.eatit.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UsersRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewUser(user: User) {
        FirebaseFirestore.getInstance().collection("users").add(user)
    }

    @Composable
    fun getUser(): SnapshotStateList<DocumentSnapshot> {
        var user = remember { mutableStateListOf<DocumentSnapshot>() }
        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.data.get("userId").toString()
                            .contains(Firebase.auth.currentUser?.uid.toString(), ignoreCase = true)
                    ) {
                        user.add(document)
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return user
    }
}