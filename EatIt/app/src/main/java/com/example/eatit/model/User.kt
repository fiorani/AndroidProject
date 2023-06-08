package com.example.eatit.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class User(
    var id: String = "",
    var name: String = "",
    var mail: String = "",
    var photo: String = "",
    var age: Int = 0,
    var restaurateur: Boolean = false,
    var position: String = "",
    var favouriteRestaurants: ArrayList<String> = ArrayList(mutableListOf()),
) {
    constructor(
        name: String,
        mail: String,
        photo: String,
        age: Int,
        position: String,
        restaurateur: Boolean
    ) : this() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            this.id = user.uid
        }
        this.name = name
        this.mail = mail
        this.photo = photo
        this.age = age
        this.restaurateur = restaurateur
        this.position = position
    }
}