package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

/**
 * Model POJO for a rating.
 */
data class Rating(
    var userId: String? = null,
    var userName: String? = null,
    var rating: Double = 0.toDouble(),
    var text: String? = null,
    @ServerTimestamp var timestamp: Date? = null,
) {

    constructor(rating: Double, text: String) : this() {
        val user = Firebase.auth.currentUser
        this.userId = user?.uid
        this.userName = user?.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user?.email
        }

        this.rating = rating
        this.text = text
    }
}
