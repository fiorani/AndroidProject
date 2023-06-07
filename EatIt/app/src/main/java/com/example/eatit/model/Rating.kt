package com.example.eatit.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Rating(
    var userId: String? = null,
    var rating: Float = 0.0f,
    var text: String? = null,
    var id: String? = null,
    @ServerTimestamp var timestamp: Date? = null,
)