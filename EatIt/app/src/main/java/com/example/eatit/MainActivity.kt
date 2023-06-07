package com.example.eatit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var auth: FirebaseAuth
    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager: ConnectivityManager
    private var queue: RequestQueue? = null
    val location = mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))
    val warningViewModel by viewModels<WarningViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }
        }
        locationRequest =
            LocationRequest.Builder(10_000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                val userViewModel by viewModels<UsersViewModel>()
                userViewModel.setLocation(p0.locations.last())
                location.value = LocationDetails(
                    p0.locations.last().latitude,
                    p0.locations.last().longitude
                )
            }
        }
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                sendRequest(location.value, connectivityManager)
                warningViewModel.setConnectivitySnackBarVisibility(false)
            }

            override fun onLost(network: Network) {
                warningViewModel.setConnectivitySnackBarVisibility(true)
            }
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val theme = sharedPref.getString("THEME_KEY", getString(R.string.light_theme))
        startLocationUpdates()
        setContent {
            EatItTheme(darkTheme = theme=="Dark") {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val (theme, onThemeChanged) = remember { mutableStateOf(theme) }
                    //startLocationUpdates()
                    NavigationApp(
                        warningViewModel = warningViewModel,
                        signIn = ::signIn,
                        createAccount = ::createAccount,
                        sharedPref = sharedPref,
                        theme = theme,
                        onOptionSelected = onThemeChanged,
                        startLocationUpdates = ::startSendRequest
                    )
                }
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            }
        }
    }

    private fun startSendRequest() {
        if (isOnline(connectivityManager = connectivityManager)) {
            sendRequest(location.value, connectivityManager)
        } else {
            warningViewModel.setConnectivitySnackBarVisibility(true)
        }
    }

    private fun sendRequest(location: LocationDetails, connectivityManager: ConnectivityManager) {
        val userViewModel by viewModels<UsersViewModel>()
        queue = Volley.newRequestQueue(this)
        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                +location.latitude + "," + location.longitude + "&key=AIzaSyAtkgSO0EAakNnErsYTuO1ORfA4QFsnqiw"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val results = response.getJSONArray("results")
                if (results.length() > 0) {
                    val address = results.getJSONObject(0).getString("formatted_address")
                    userViewModel.setPosition(address)
                }
            },
            { error ->
                Log.d("MAINACTIVITY-SENDREQUEST", error.toString())
            }
        )
        jsonObjectRequest.tag = TAG
        queue?.add(jsonObjectRequest)

    }

    override fun onStop() {
        super.onStop()
        queue?.cancelAll(TAG)
    }

    private fun startLocationUpdates() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        when {
            //permission already granted
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                val gpsEnabled = checkGPS()
                if (gpsEnabled) {
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } else {
                    warningViewModel.setGPSAlertDialogVisibility(true)
                }
            }
            //permission already denied
            shouldShowRequestPermissionRationale(permission) -> {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }

            else -> {
                //first time: ask for permissions
                locationPermissionRequest.launch(
                    permission
                )
            }
        }
    }

    private fun checkGPS(): Boolean {
        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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
        restaurateur: Boolean,
        onNextButtonClicked: () -> Unit
    ) {
        val usersViewModel by viewModels<UsersViewModel>()
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        usersViewModel.addNewUser(
                            User(
                                auth.currentUser!!.uid,
                                name,
                                email,
                                photo,
                                age,
                                address,
                                restaurateur
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


