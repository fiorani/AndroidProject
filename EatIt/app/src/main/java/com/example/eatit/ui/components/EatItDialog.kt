package com.example.eatit.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.eatit.R
import com.example.eatit.data.AndroidFileSystem
import com.example.eatit.data.PhotoPicker
import com.example.eatit.model.FileDetails
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.example.eatit.utilities.createImageFile
import com.example.eatit.utilities.saveImage
import com.example.eatit.utilities.toOkioPath
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Objects
@androidx.annotation.OptIn(androidx.core.os.BuildCompat.PrereleaseSdkCheck::class)
@Composable
fun AddRestaurantScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { usersViewModel.position }
    val photo = remember { mutableStateOf("") }
    val numRatings = 0
    val avgRating = 0.0f
    Card {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = stringResource(R.string.add_rest),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            OutlinedTextField(
                value = address,
                onValueChange = { newText -> address = newText },
                label = { Text(stringResource(R.string.address2)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 2.dp),
                trailingIcon = {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = stringResource(R.string.localized2),
                        Modifier
                            .clickable(onClick = {
                                startLocationUpdates()
                            })
                    )
                }
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.restaurant_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 2.dp)
            )

            Spacer(modifier = Modifier.size(20.dp))
            val context = LocalContext.current
            val fileSystem = AndroidFileSystem(LocalContext.current)
            var selectedFiles by remember { mutableStateOf<List<FileDetails>>(emptyList()) }
            val photoPicker = rememberLauncherForActivityResult(PhotoPicker()) { uris ->
                selectedFiles = uris.map { uri ->
                    val path = uri.toOkioPath()
                    val metadata = fileSystem.metadataOrNull(path) ?: return@map null
                    FileDetails(uri, path, metadata)
                }.filterNotNull()
            }
            ImageCard(
                photo.value,
                modifier = Modifier
                    .padding(20.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.Center
            ) {
                EatItButton(
                    modifier = Modifier
                        .width(150.dp)
                        .padding(2.dp),
                    text = stringResource(R.string.gallery),
                    function = {
                        photoPicker.launch(
                            PhotoPicker.Args(
                                PhotoPicker.Type.IMAGES_ONLY,
                                1
                            )
                        )
                    },
                    icon = Icons.Filled.Photo
                )
                if (selectedFiles.isNotEmpty()) {
                    LaunchedEffect(Unit) {
                        photo.value = restaurantsViewModel.uploadPhoto(
                            saveImage(
                                context.applicationContext.contentResolver,
                                selectedFiles[0].uri
                            )!!
                        ).toString()
                    }
                }
            }

            Button(
                onClick = {
                    restaurantsViewModel.addNewRestaurant(
                        Restaurant(
                            name = name,
                            address = address,
                            photo = photo.value,
                            numRatings = numRatings,
                            avgRating = avgRating,
                            userId = Firebase.auth.uid!!
                        )
                    )
                    onNextButtonClicked()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Text(text = stringResource(id = R.string.save))
            }
            Spacer(Modifier.size(25.dp))
        }
    }
}


@Composable
fun AddProductScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
) {
    val data = LocalContext.current.resources.getStringArray(R.array.categories).toList()

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    val selectedSection = remember { mutableStateOf(data.firstOrNull() ?: "") }

    if (restaurantsViewModel.productSelected.id != "") {
        name = restaurantsViewModel.productSelected.name
        description = restaurantsViewModel.productSelected.description
        price = restaurantsViewModel.productSelected.price.toString()
        selectedSection.value = restaurantsViewModel.productSelected.section
    }

    Card {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            val rows = data.chunked(3)
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.choose_section),
                modifier = Modifier.padding(10.dp, 5.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
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
                                text = buildAnnotatedString { append(option) },
                                onClick = { selectedSection.value = option }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.build_dish),
                modifier = Modifier.padding(10.dp, 5.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.restaurant_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp)
            )

            Spacer(modifier = Modifier.size(5.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.restaurant_description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp)
            )

            Spacer(modifier = Modifier.size(5.dp))

            val decimalOnlyRegex = Regex("^\\d+(\\.\\d{0,2})?\$")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
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
                    modifier = Modifier
                        .width(150.dp)
                        .padding(10.dp, 0.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Euro,
                            contentDescription = "Euro icon",
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.size(15.dp))

            Button(
                onClick = {
                    if (price == "") price = "0"
                    if (restaurantsViewModel.productSelected.id != "") {
                        restaurantsViewModel.setProduct(
                            Product(
                                name = name,
                                description = description,
                                price = price.toFloat(),
                                section = selectedSection.value
                            )
                        )
                    } else {
                        restaurantsViewModel.addNewProduct(
                            Product(
                                name = name,
                                description = description,
                                price = price.toFloat(),
                                section = selectedSection.value
                            )
                        )
                    }
                    onNextButtonClicked()
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(Icons.Default.Save, stringResource(id = R.string.save))
                Text(
                    modifier = Modifier.padding(3.dp, 0.dp),
                    text = stringResource(id = R.string.save)
                )
            }
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.core.os.BuildCompat.PrereleaseSdkCheck::class)
@Composable
fun EditRestaurantDialog(onDismissRequest: () -> Unit, restaurantsViewModel: RestaurantsViewModel) {
    val restaurant = restaurantsViewModel.restaurantSelected
    var txtName by rememberSaveable { mutableStateOf(restaurant.name) }
    var phone by rememberSaveable { mutableStateOf(restaurant.phone) }
    val photo = remember { mutableStateOf(restaurant.photo) }
    AlertDialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(30.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 5.dp),
                text = stringResource(R.string.modify_restaurant),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = txtName,
                    onValueChange = { txtName = it },
                    label = { Text(stringResource(R.string.rest_name)) }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = VisualTransformation.None,
                    label = { Text(stringResource(R.string.phone_number2)) },
                )

                //Photo
                val context = LocalContext.current
                val fileSystem = AndroidFileSystem(LocalContext.current)
                var selectedFiles by remember { mutableStateOf<List<FileDetails>>(emptyList()) }
                val photoPicker = rememberLauncherForActivityResult(PhotoPicker()) { uris ->
                    selectedFiles = uris.map { uri ->
                        val path = uri.toOkioPath()
                        val metadata = fileSystem.metadataOrNull(path) ?: return@map null
                        FileDetails(uri, path, metadata)
                    }.filterNotNull()
                }
                ImageCard(
                    photo.value,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = Color.Black.copy(alpha = 0.5f)
                            )
                        }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp), horizontalArrangement = Arrangement.Center
                ) {
                    EatItButton(
                        modifier = Modifier
                            .width(150.dp)
                            .padding(2.dp),
                        text = stringResource(R.string.gallery),
                        function = {
                            photoPicker.launch(
                                PhotoPicker.Args(
                                    PhotoPicker.Type.IMAGES_ONLY,
                                    1
                                )
                            )
                        },
                        icon = Icons.Filled.Photo
                    )
                    if (selectedFiles.isNotEmpty()) {
                        LaunchedEffect(Unit) {
                            photo.value = restaurantsViewModel.uploadPhoto(
                                saveImage(
                                    context.applicationContext.contentResolver,
                                    selectedFiles[0].uri
                                )!!
                            ).toString()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EatItButton(
                    function = {
                        if (txtName != "") restaurant.name = txtName
                        if (phone != "") restaurant.phone = phone
                        if (photo.value != "") restaurant.photo = photo.value
                        restaurantsViewModel.setRestaurant()
                        onDismissRequest()
                    },
                    text = stringResource(R.string.save3),
                    enabled = true,
                    icon = Icons.Default.Save
                )
                TextButton(
                    modifier = Modifier.padding(10.dp, 10.dp),
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = stringResource(R.string.back2),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
    }
}