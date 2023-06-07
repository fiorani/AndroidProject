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

import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class Filter() {
    @Composable
    fun filterDistance(restaurant: Restaurant, user: User, distance: String): Boolean {
        val positionRestaurant =
            Geocoder(LocalContext.current).getFromLocationName(restaurant.address.toString(), 1)
        val positionUser =
            Geocoder(LocalContext.current).getFromLocationName(user.position.toString(), 1)
        var locationRestaurant: Location = Location("restaurant")
        locationRestaurant.latitude = positionRestaurant?.get(0)!!.latitude
        locationRestaurant.longitude = positionRestaurant?.get(0)!!.longitude
        var locationUser: Location = Location("user")
        locationUser.latitude = positionUser?.get(0)!!.latitude
        locationUser.longitude = positionUser?.get(0)!!.longitude
        val distanceBetween = locationRestaurant.distanceTo(locationUser)
        return distanceBetween <= distance.toFloat()
    }
}


