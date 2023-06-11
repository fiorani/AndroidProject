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
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class Filter(
    var favorite: Boolean = false,
    var distance: Int = 25000,
    var sort: String? = "Predefinito",
) {

    fun filterDistance(restaurant: Restaurant, user: User, distance: Int,context:Context): Boolean {
        val positionRestaurant =
            Geocoder(context).getFromLocationName(restaurant.address, 1)
        val positionUser =
            Geocoder(context).getFromLocationName(user.position, 1)
        val locationRestaurant = Location("restaurant")
        locationRestaurant.latitude = positionRestaurant?.get(0)!!.latitude
        locationRestaurant.longitude = positionRestaurant.get(0)!!.longitude
        Log.d("distance", locationRestaurant.toString())
        val locationUser = Location("user")
        locationUser.latitude = positionUser?.get(0)!!.latitude
        locationUser.longitude = positionUser.get(0)!!.longitude
        Log.d("distance", locationUser.toString())
        val distanceBetween = locationRestaurant.distanceTo(locationUser)
        restaurant.distance = distanceBetween.toInt()
        Log.d("distance", distanceBetween.toString())
        if (distanceBetween <= distance) {
            return true
        }
        return false
    }

    fun sort(restaurant: List<Restaurant>,sort:String?):List<Restaurant> {
        if (sort == "Distanza") {
            Log.d("Distanza", restaurant.sortedBy { it.distance }.toString())
            return restaurant.sortedBy { it.distance }
        }
        if (sort == "Alfabetico") {
            Log.d("Alfabetico", restaurant.sortedBy { it.name }.toString())
            return restaurant.sortedBy { it.name }
        }
        return restaurant
    }
}


