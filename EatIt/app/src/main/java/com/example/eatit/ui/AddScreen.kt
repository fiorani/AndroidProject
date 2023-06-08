package com.example.eatit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.example.eatit.utilities.createImageFile
import com.example.eatit.utilities.saveImage
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


@Composable
fun AddRestaurantScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { usersViewModel.position }
    var photo by rememberSaveable { mutableStateOf("") }
    var numRatings = 0
    var avgRating = 0.0f
    LaunchedEffect(Unit) {
        city = usersViewModel.getPosition()
    }

    Scaffold { paddingValues ->
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
                    value = city,
                    onValueChange = { newText ->
                        city = newText
                    },
                    label = { Text("Username") },
                    modifier = Modifier.weight(4f)
                )
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Localized",
                    Modifier
                        .weight(1f)
                        .clickable(onClick = {
                            startLocationUpdates()
                        })
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
                LaunchedEffect(Unit) {
                    photo=restaurantsViewModel.uploadPhoto(saveImage(context.applicationContext.contentResolver, capturedImageUri)!!).toString()
                }
            }

            Button(
                onClick = {
                    restaurantsViewModel.addNewRestaurant(
                        Restaurant(
                            name = name,
                            address = city,
                            photo = photo,
                            numRatings = numRatings,
                            avgRating = avgRating,
                            userId = Firebase.auth.uid!!
                        )
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

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {


            val data = LocalContext.current.resources.getStringArray(R.array.categories).toList()

            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(data.size) { item ->
                    AssistChip(
                        modifier = Modifier.padding(30.dp, 1.dp),
                        onClick = { /* Do something! */ },
                        label = {
                            Text(
                                textAlign = TextAlign.Center,
                                text = data[item]
                            )
                        }
                    )
                }
            }

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
                label = { Text(stringResource(id = R.string.restaurant_price)) },
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
                LaunchedEffect(Unit) {
                   photoURI= restaurantsViewModel.uploadPhoto(saveImage(context.applicationContext.contentResolver, capturedImageUri)!!).toString()
                }
            }
            Button(
                onClick = {
                    if (restaurantsViewModel.productSelected.id=="") {
                        restaurantsViewModel.setProduct(
                            Product(
                                name = name,
                                description = description,
                                photo = photoURI
                            )
                        )
                    } else {
                        restaurantsViewModel.addNewProduct(
                            Product(
                                name = name,
                                description = description,
                                photo = photoURI,
                                section = ""
                            )
                        )
                    }
                    onNextButtonClicked()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Text(text = stringResource(id = R.string.save))
            }


        }
    }
}
