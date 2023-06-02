package com.example.eatit.ui

import android.location.Geocoder
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.eatit.data.LocationDetails
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import okhttp3.internal.wait


@Composable
fun MapScreen(
    startLocationUpdates: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val restaurantsCollection = db.collection("restaurants")
    val restaurants = remember { mutableStateListOf<DocumentSnapshot>() }
    restaurantsCollection.get()
        .addOnSuccessListener { querySnapshot ->
            restaurants.addAll(querySnapshot.documents)
        }
        .addOnFailureListener { exception ->
            println("Error getting restaurants: $exception")
        }
    var myPosition by rememberSaveable { restaurantsViewModel.restaurantFromGPS }
    Log.d("currentLocation", myPosition)
    var cameraPositionState = rememberCameraPositionState{
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize()
            ) {
                for ( restaurant in restaurants) {
                    var position = Geocoder(context).getFromLocationName(restaurant["city"].toString(), 1)
                    if (position != null) {
                        if (position.size > 0){
                            Marker(
                                state = MarkerState(position = LatLng(position?.get(0)?.latitude!!, position?.get(0)?.longitude!!)),
                                title = restaurant["name"].toString()
                            )
                        }
                    }

                }
            }
        }
    }
}
