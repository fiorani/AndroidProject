package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsersRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewUser(user: User) {
        FirebaseFirestore.getInstance().collection("users").add(user)
    }

    fun setPosition(position: String) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("id", Firebase.auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                it.documents.firstOrNull()?.reference?.update("position", position)
            }
    }

    suspend fun getPosition(): String =
        withContext(Dispatchers.IO) {
            try {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("id", Firebase.auth.currentUser?.uid.toString()).get().await()
                    .documents.firstOrNull()?.get("position").toString()
            } catch (e: Exception) {
                throw e
            }
        }


    suspend fun getUser(): User =
        withContext(Dispatchers.IO) {
            try {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("id", Firebase.auth.currentUser?.uid.toString())
                    .get().await().documents.firstOrNull()?.toObject(User::class.java)!!
            } catch (e: Exception) {
                throw e
            }
        }

    suspend fun getUserById(userId: String): User =
        withContext(Dispatchers.IO) {
            try {
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("id", userId)
                    .get().await().documents.firstOrNull()?.toObject(User::class.java)!!
            } catch (e: Exception) {
                throw e
            }
        }
}