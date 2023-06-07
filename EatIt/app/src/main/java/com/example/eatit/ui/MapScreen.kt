package com.example.eatit.ui

import android.content.ContentValues.TAG
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.eatit.model.Restaurant
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(
    restaurantsViewModel: RestaurantsViewModel,
    usersViewModel: UsersViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    val markers = remember { mutableStateListOf<MarkerInfo>() }
    LaunchedEffect(Unit) {
        restaurants = restaurantsViewModel.getRestaurants()
    }
    LaunchedEffect(restaurants) {
        for (restaurant in restaurants) {
            val position = withContext(Dispatchers.IO) {
                Geocoder(context).getFromLocationName(restaurant.city.toString(), 1)
            }
            if (position != null && position.size > 0) {
                val latitude = position[0].latitude
                val longitude = position[0].longitude
                markers.add(
                    MarkerInfo(
                        restaurant.name.toString(),
                        LatLng(latitude, longitude)
                    )
                )
            }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    val locationSource = remember {
        object : LocationSource {
            private var listener: LocationSource.OnLocationChangedListener? = null
            override fun activate(listener: LocationSource.OnLocationChangedListener) {
                this.listener = listener
            }

            override fun deactivate() {
                listener = null
            }

            fun onLocationChanged(location: Location) {
                listener?.onLocationChanged(location)
            }
        }
    }
    LaunchedEffect(usersViewModel.location.value) {
        Log.d(TAG, "Updating blue dot on map...")
        locationSource.onLocationChanged(usersViewModel.location.value)
        cameraPositionState.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(
                    LatLng(
                        usersViewModel.location.value.latitude,
                        usersViewModel.location.value.longitude
                    ), 20f
                )
            ), 1_000
        )
    }
    var isMapLoaded by remember { mutableStateOf(false) }
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    isMapLoaded = true
                },
                locationSource = locationSource,
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                if (!isMapLoaded) {
                } else {
                    markers.forEach { markerInfo ->
                        Marker(
                            state = rememberMarkerState(
                                position = markerInfo.position
                            ),
                            title = markerInfo.title
                        )
                    }
                }
            }
        }
    }
}

data class MarkerInfo(
    val title: String,
    val position: LatLng
)

