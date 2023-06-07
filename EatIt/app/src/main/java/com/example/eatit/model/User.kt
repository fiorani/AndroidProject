package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class User(
    var id: String? = null,
    var name: String? = null,
    var mail: String? = null,
    var photo: String? = null,
    var age: Int = 0,
    var address: String? = null,
    var restaurateur: Boolean = false,
    var position: String? = null
) {
    constructor(
        mail: String,
        photo: String,
        age: Int,
        address: String,
        restaurateur: Boolean
    ) : this() {
        val user = Firebase.auth.currentUser
        this.id = user?.uid
        this.name = user?.displayName
        if (TextUtils.isEmpty(this.name)) {
            this.name = user?.email
        }
        this.mail = mail
        this.photo = photo
        this.age = age
        this.address = address
        this.restaurateur = restaurateur
    }
}