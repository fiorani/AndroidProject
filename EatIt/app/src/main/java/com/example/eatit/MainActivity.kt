package com.example.eatit

import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.eatit.data.LocationDetails
import com.example.eatit.model.User
import com.example.eatit.ui.theme.EatItTheme
import com.example.eatit.viewModel.UsersViewModel
import com.example.eatit.viewModel.WarningViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var locationCallback: LocationCallback
    private var queue: RequestQueue? = null
    private val warningViewModel by viewModels<WarningViewModel>()
    private val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
    private var requestingLocationUpdates = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    var location = mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                location.value = LocationDetails(
                    p0.locations.first().latitude,
                    p0.locations.first().longitude
                )
                stopLocationUpdates()
                if (isOnline(connectivityManager = connectivityManager)) {
                    sendRequest(location.value, connectivityManager)
                } else {
                    warningViewModel.setConnectivitySnackBarVisibility(true)
                }
            }
        }
        updateValuesFromBundle(savedInstanceState)
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (requestingLocationUpdates) {
                    sendRequest(location.value, connectivityManager)
                    warningViewModel.setConnectivitySnackBarVisibility(false)
                }
            }
            override fun onLost(network: Network) {
                warningViewModel.setConnectivitySnackBarVisibility(true)
            }
        }
        setContent {
            EatItTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //startLocationUpdates()
                    NavigationApp(
                        warningViewModel = warningViewModel,
                        signIn = ::signIn,
                        createAccount = ::createAccount,
                        startLocationUpdates = ::startLocationUpdates,
                    )
                }
                if (requestingLocationUpdates) {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                }
            }
        }
    }
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.

        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY)
        }
    }
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()

    }
    fun sendRequest(location: LocationDetails, connectivityManager: ConnectivityManager) {
        val userViewModel by viewModels<UsersViewModel>()
        queue = Volley.newRequestQueue(this)
        val url = "https://nominatim.openstreetmap.org/reverse?lat=" + location.latitude +
                "&lon=" + location.longitude + "&format=jsonv2&limit=1"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                userViewModel.setPosition(response.getString("display_name"))
                connectivityManager.unregisterNetworkCallback(networkCallback)
                requestingLocationUpdates = false
            },
            { error ->
                Log.d("MAINACTIVITY-SENDREQUEST", error.toString())
            }
        )
        jsonObjectRequest.tag = TAG
        queue?.add(jsonObjectRequest)
    }
    private fun isOnline(connectivityManager: ConnectivityManager): Boolean {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        ) {
            return true
        }
        return false
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()

    }
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
            super.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
        queue?.cancelAll(TAG)
    }

    override fun onStart() {
        super.onStart()
    }

    companion object {
        private const val TAG = "OSM_REQUEST"
    }

    private fun createAccount(
        email: String,
        password: String,
        name: String,
        photo: String,
        age: Int,
        address: String,
        isRestaurateur: Boolean,
        onNextButtonClicked: () -> Unit
    ) {
        val usersViewModel by viewModels<UsersViewModel>()
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        usersViewModel.addNewUser(
                            User(
                                auth.currentUser!!.uid,
                                name,
                                email,
                                photo,
                                age,
                                address,
                                isRestaurateur
                            )
                        )
                        signIn(email, password, onNextButtonClicked)
                    } else {
                        task.exception?.let { errorToast("createUserWithEmail:failure", it) }
                    }
                }
        } catch (e: IllegalArgumentException) {
            errorToast("signInWithEmail:failure", e)
        }
    }

    private fun signIn(email: String, password: String, onNextButtonClicked: () -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        onNextButtonClicked()
                    } else {
                        task.exception?.let { errorToast("signInWithEmail:failure", it) }
                    }
                }
        } catch (e: IllegalArgumentException) {
            errorToast("signInWithEmail:failure", e)
        }
    }

    private fun errorToast(errorMsg: String, e: Exception) {
        // If sign in fails, display a message to the user.
        Log.w(TAG, errorMsg, e)
        Toast.makeText(
            baseContext,
            "Authentication failed.",
            Toast.LENGTH_SHORT,
        ).show()
    }
}

