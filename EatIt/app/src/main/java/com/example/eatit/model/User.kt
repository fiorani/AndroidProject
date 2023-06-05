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
) {
    constructor(mail: String, photo: String, age: Int) : this() {
        val user = Firebase.auth.currentUser
        this.userId = user?.uid
        this.userName = user?.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user?.email
        }
        this.userEmail = mail
        this.photo = photo
        this.age = age
    }
}