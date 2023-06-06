package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class User(
    var userId: String? = null,
    var userName: String? = null,
    var userEmail: String? = null,
    var photo: String? = null,
    var age: Int = 0,
    var address: String? = null,
    var isRestaurateur : Boolean = false
) {
    constructor(mail: String, photo: String, age: Int, address: String, isRestaurateur: Boolean) : this() {
        val user = Firebase.auth.currentUser
        this.userId = user?.uid
        this.userName = user?.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user?.email
        }
        this.userEmail = mail
        this.photo = photo
        this.age = age
        this.address = address
        this.isRestaurateur = isRestaurateur
    }
}