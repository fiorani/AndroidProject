package com.example.eatit.data

import androidx.annotation.WorkerThread
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

    fun setPosition(position: String) {
        val user = getUser()
        FirebaseFirestore.getInstance().collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.data.get("userId").toString()
                            .contains(Firebase.auth.currentUser?.uid.toString(), ignoreCase = true)
                    ) {
                        FirebaseFirestore.getInstance().collection("users").document(user[0].id)
                            .update("userPosition", position)
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
    }

    fun getUser(): List<DocumentSnapshot> {
        val user = mutableListOf<DocumentSnapshot>()
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