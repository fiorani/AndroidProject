package com.example.eatit.util

import com.example.eatit.model.Rating

/**
 * Utilities for Ratings.
 */
object RatingUtil {

    fun getAverageRating(ratings: List<Rating>): Double {
        var sum = 0.0

        for (rating in ratings) {
            sum += rating.rating
        }

        return sum / ratings.size
    }
}
