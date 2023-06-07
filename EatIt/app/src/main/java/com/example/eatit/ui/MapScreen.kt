package com.example.eatit.ui

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.eatit.model.Restaurant
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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
   /* LaunchedEffect(Unit) {
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
    }*/
    val cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    val locationSource = MyLocationSource()
    var currentLocation by remember { mutableStateOf(Location("MyLocationProvider")) }
    val locationRequest = LocationRequest.Builder(10_000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMinUpdateIntervalMillis(10_000)
        .setMinUpdateDistanceMeters(10.0f)
        .setWaitForAccurateLocation(true)
        .build()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            currentLocation = p0.lastLocation!!
        }
    }
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
    LaunchedEffect(currentLocation) {
        Log.d(TAG, "Updating blue dot on map...")
        locationSource.onLocationChanged(currentLocation)
        Log.d(TAG, "Updating camera position...")
        /*val cameraPosition = CameraPosition.fromLatLngZoom(
            LatLng(currentLocation.latitude, currentLocation.longitude), 20f)
        cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(cameraPosition), 1_000)*/
    }

    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                locationSource = locationSource,
                properties  = mapProperties
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

@Stable
class MyLocationSource : LocationSource {
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
