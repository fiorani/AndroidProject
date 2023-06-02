package com.example.eatit.ui

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.eatit.viewModel.RestaurantsViewModel
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


@Composable
fun MapScreen(
    startLocationUpdates: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            val camera = LatLng(1.35, 103.87)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(camera, 10f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {

                for ( restaurant in restaurants) {
                    val name = restaurant["name"].toString()
                    val address = restaurant["city"].toString()
                    val context = LocalContext.current
                    val position = Geocoder(context).getFromLocationName(address, 1)
                    if (position != null) {
                        if (position.size > 0){
                            val latitude = position?.get(0)?.latitude
                            val longitude = position?.get(0)?.longitude
                            Marker(
                                state = MarkerState(position = LatLng(latitude!!, longitude!!)),
                                title = name
                            )
                        }
                    }

                }
            }
        }
    }
}
