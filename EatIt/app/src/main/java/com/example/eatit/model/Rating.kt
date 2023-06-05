package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

data class Rating(
    var userId: String? = null,
    var userName: String? = null,
    var rating: Double = 0.toDouble(),
    var text: String? = null,
    @ServerTimestamp var timestamp: Date? = null,
) {

    constructor(rating: Double, text: String) : this() {
        this.userId = Firebase.auth.uid
        this.userName =  Firebase.auth.currentUser?.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = Firebase.auth.currentUser?.email
        }
        this.rating = rating
        this.text = text
    }
}
