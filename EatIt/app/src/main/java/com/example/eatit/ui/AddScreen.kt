package com.example.eatit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.graphics.Color
import com.example.eatit.R
import com.example.eatit.utilities.createImageFile
import com.example.eatit.data.Restaurant
import com.example.eatit.utilities.saveImage
import com.example.eatit.viewModel.RestaurantsViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRestaurantScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    startLocationUpdates: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { restaurantsViewModel.restaurantFromGPS }
    var description by rememberSaveable { mutableStateOf("") }
    var photoURI by rememberSaveable { mutableStateOf("") }

    Scaffold(
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { newText ->
                        address = newText
                    },
                    label = {
                        Text(stringResource(id = R.string.restaurant_address))
                    },
                    modifier = Modifier.weight(4f)
                )

                Icon(
                    Icons.Filled.LocationSearching,
                    contentDescription = "get gps",
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = startLocationUpdates)
                )
            }
            Spacer(modifier = Modifier.size(15.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.restaurant_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(15.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.restaurant_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(15.dp))

            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )

            var capturedImageUri by remember {
                mutableStateOf<Uri>(Uri.EMPTY)
            }

            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                    if (isSuccess) {
                        capturedImageUri = uri
                    }
                }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (it) {
                    cameraLauncher.launch(uri)
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

            Button(
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Take a picture")
            }

            Spacer(modifier = Modifier.size(15.dp))

            if (capturedImageUri.path?.isNotEmpty() == true) {
                AsyncImage(model = ImageRequest.Builder(context)
                    .data(capturedImageUri)
                    .crossfade(true)
                    .build(), contentDescription = "image taken")

                photoURI = saveImage(context.applicationContext.contentResolver, capturedImageUri)
            }

            Button(
                onClick = {
                    restaurantsViewModel.addNewRestaurant(
                        Restaurant(restaurantName = name, restaurantAddress = address, restaurantDescription = description, restaurantPhoto = photoURI)
                    )
                    onNextButtonClicked()
                },
                colors = ButtonDefaults.buttonColors(Color.Green),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var photoURI by rememberSaveable { mutableStateOf("") }

    Scaffold(
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.restaurant_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(15.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.restaurant_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(15.dp))


            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(stringResource(id = R.string.restaurant_description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.size(15.dp))
            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )

            var capturedImageUri by remember {
                mutableStateOf<Uri>(Uri.EMPTY)
            }

            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                    if (isSuccess) {
                        capturedImageUri = uri
                    }
                }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (it) {
                    cameraLauncher.launch(uri)
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

            Button(
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Take a picture")
            }

            Spacer(modifier = Modifier.size(15.dp))

            if (capturedImageUri.path?.isNotEmpty() == true) {
                AsyncImage(model = ImageRequest.Builder(context)
                    .data(capturedImageUri)
                    .crossfade(true)
                    .build(), contentDescription = "image taken")

                photoURI = saveImage(context.applicationContext.contentResolver, capturedImageUri)
            }
        }
    }
}