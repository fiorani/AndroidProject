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
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext


@Composable
fun MapScreen(

    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel
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

    var myPosition by rememberSaveable { mutableStateOf("") }
    var cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    myPosition = usersViewModel.userPosition.value
    val currentLocation = Geocoder(context).getFromLocationName(myPosition, 1)
    if (currentLocation != null && currentLocation.size > 0) {
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(currentLocation[0].latitude, currentLocation[0].longitude), 9f
            )
        }
    }

    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                //locationSource = locationSource,
            ) {
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

data class MarkerInfo(
    val title: String,
    val position: LatLng
)
private class MyLocationSource : LocationSource {

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