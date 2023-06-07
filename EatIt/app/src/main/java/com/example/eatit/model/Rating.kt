package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

data class Rating(
    var userId: String? = null,
    var rating: Float = 0.0f,
    var text: String? = null,
    var id: String? = null,
    @ServerTimestamp var timestamp: Date? = null,
)