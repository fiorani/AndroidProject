/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.eatit.model

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log

class Filter(
    var favorite: Boolean = false,
    var sort: String = "Predefinito",
) {
    fun sort(restaurant: List<Restaurant>, sort: String?): List<Restaurant> {
        if (sort == "Alfabetico") {
            return restaurant.sortedBy { it.name }
        }
        return restaurant
    }

    fun filterFavorite(restaurants: List<Restaurant>, favorite: List<String>): List<Restaurant> {
        val filteredRestaurants = mutableListOf<Restaurant>()
        for (restaurant in restaurants) {
            if (favorite.contains(restaurant.id)) {
                filteredRestaurants.add(restaurant)
            }
        }
        return filteredRestaurants
    }
}


