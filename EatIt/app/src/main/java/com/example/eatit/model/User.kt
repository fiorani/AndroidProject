package com.example.eatit.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class User(
    var id: String = "",
    var name: String = "",
    var mail: String = "",
    var photo: String = "",
    var birth: String = "",
    var restaurateur: Boolean = false,
    var position: String = "",
    var phone: String = "",
    var favouriteRestaurants: ArrayList<String> = ArrayList(mutableListOf()),
) {
    constructor(
        name: String,
        mail: String,
        photo: String,
        birth: String,
        restaurateur: Boolean,
        position: String,
        phone: String,
        favouriteRestaurants: ArrayList<String>
    ) : this() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            this.id = user.uid
        }
        this.name = name
        this.mail = mail
        this.photo = photo
        this.birth = birth
        this.restaurateur = restaurateur
        this.position = position
        this.phone = phone
        this.favouriteRestaurants = favouriteRestaurants
    }
}