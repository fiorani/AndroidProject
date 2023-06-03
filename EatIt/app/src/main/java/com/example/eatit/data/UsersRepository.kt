package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewUser(user: User) {
        FirebaseFirestore.getInstance().collection("users").add(user)
    }
}