package com.example.eatit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    val numRatings = 0
    val avgRating = 0.0f
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
            val selectedSection = remember { mutableStateOf(data.firstOrNull() ?: "") }
            val rows = data.chunked(3)
            Row {
                Text(
                    text = "Choose a section:",
                    modifier = Modifier.padding(10.dp, 5.dp).weight(1f),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                rows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                            row.forEach { option ->
                            RadioButton(
                                selected = selectedSection.value == option,
                                onClick = { selectedSection.value = option }
                            )
                            ClickableText(
                                modifier = Modifier.padding(start = 8.dp),
                                text = buildAnnotatedString { append(option) },
                                onClick = { selectedSection.value = option }
                            )
                        }
                    }
                }
            }
            Row {
                Text(
                    text = "Build your dish:",
                    modifier = Modifier.padding(10.dp, 5.dp).weight(1f),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
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

            val decimalOnlyRegex = Regex("^\\d+(\\.\\d{0,2})?\$")

            Row (modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { newValue ->
                        if (newValue.matches(decimalOnlyRegex)) {
                            // Aggiorna il valore solo se il testo inserito corrisponde al formato desiderato
                            price = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = VisualTransformation.None,
                    label = { Text(stringResource(id = R.string.restaurant_price)) },
                    modifier = Modifier.width(150.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Euro,
                            contentDescription = "Euro icon",
                        )
                    }
                )
            }
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
                    if (restaurantsViewModel.productSelected.id!="") {
                        restaurantsViewModel.setProduct(
                            Product(
                                name = name,
                                description = description,
                                photo = photoURI,
                                price=price.toFloat(),
                                section = selectedSection.value
                            )
                        )
                    } else {
                        restaurantsViewModel.addNewProduct(
                            Product(
                                name = name,
                                description = description,
                                photo = photoURI,
                                price=price.toFloat(),
                                section = selectedSection.value
                            )
                        )
                    }
                    onNextButtonClicked()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(Icons.Default.Save, stringResource(id = R.string.save))
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

