package com.example.eatit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger

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
                Log.d("MAINACTIVITY", p0.locations.last().toString())
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
            EatItTheme(darkTheme = theme == "Dark") {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val (theme1, onThemeChanged) = remember { mutableStateOf(theme) }
                    //startLocationUpdates()
                    NavigationApp(
                        warningViewModel = warningViewModel,
                        signIn = ::signIn,
                        createAccount = ::createAccount,
                        sharedPref = sharedPref,
                        theme = theme1,
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
        Log.d("MAINACTIVITY-SENDREQUEST", location.toString())
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
        restaurateur: Boolean,
        address: String,
        phone:String,
        onNextButtonClicked: () -> Unit
    ) {
        val usersViewModel by viewModels<UsersViewModel>()
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        usersViewModel.addNewUser(
                            User(
                                name = name,
                                mail = email,
                                photo = photo,
                                age = age,
                                position = address,
                                restaurateur = restaurateur,
                                phone = phone,
                                favouriteRestaurants = ArrayList(
                                    listOf()
                                )
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
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new FCM registration token
                            val token = task.result
                        })
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

    fun runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        Firebase.messaging.isAutoInitEnabled = true
        // [END fcm_runtime_enable_auto_init]
    }

    fun deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        val to = "a_unique_key" // the notification key
        val msgId = AtomicInteger()
        Firebase.messaging.send(
            remoteMessage(to) {
                setMessageId(msgId.get().toString())
                addData("hello", "world")
            },
        )
        // [END fcm_device_group_upstream]
    }

    fun sendUpstream() {
        val SENDER_ID = "YOUR_SENDER_ID"
        val messageId = 0 // Increment for each
        // [START fcm_send_upstream]
        val fm = Firebase.messaging
        fm.send(
            remoteMessage("$SENDER_ID@fcm.googleapis.com") {
                setMessageId(messageId.toString())
                addData("my_message", "Hello World")
                addData("my_action", "SAY_HELLO")
            },
        )
        // [END fcm_send_upstream]
    }

    fun subscribeTopics() {
        // [START subscribe_topics]
        Firebase.messaging.subscribeToTopic("weather")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }

    fun logRegToken() {
        // [START log_reg_token]
        Firebase.messaging.getToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FCM Registration token: $token"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
        // [END log_reg_token]
    }

    // [START ask_post_notifications]
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // [END ask_post_notifications]

    // [START get_store_token]
    private suspend fun getAndStoreRegToken(): String {
        val token = Firebase.messaging.token.await()
        // Add token and timestamp to Firestore for this user
        val deviceToken = hashMapOf(
            "token" to token,
            "timestamp" to FieldValue.serverTimestamp(),
        )

        // Get user ID from Firebase Auth or your own server
        Firebase.firestore.collection("fcmTokens").document(Firebase.auth.currentUser!!.uid)
            .set(deviceToken).await()
        return token
    }
    // [END get_store_token]
}


