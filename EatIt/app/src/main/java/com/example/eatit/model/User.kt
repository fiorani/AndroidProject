package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

class User (
    var userName: String? = null,
    var city: String? = null,
    var photo: String? = null,
    var age: Int = 0,
) {

    constructor(city: String, photo: String, age: Int) : this() {
        val user = Firebase.auth.currentUser
        this.userName = user?.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user?.email
        }
        this.city = city
        this.photo = photo
        this.age = age
    }
}