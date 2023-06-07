package com.example.eatit.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.patrykandpatrick.vico.core.extension.getFieldValue
import kotlinx.coroutines.selects.select
import java.util.*


@Composable
fun AddRestaurantScreen(
    onNextButtonClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { usersViewModel.userPosition }
    var category by rememberSaveable { mutableStateOf("") }
    var photo by rememberSaveable { mutableStateOf("") }
    var price = 0
    var numRatings = 0
    var avgRating = 0.toDouble()
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

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(stringResource(id = R.string.label_category)) },
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
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(capturedImageUri)
                        .crossfade(true)
                        .build(), contentDescription = "image taken"
                )

                photo = saveImage(context.applicationContext.contentResolver, capturedImageUri)
            }

            Button(
                onClick = {
                    restaurantsViewModel.addNewRestaurant(
                        Restaurant(
                            name = name,
                            city = city,
                            category = category,
                            photo = photo,
                            price = price,
                            numRatings = numRatings,
                            avgRating = avgRating
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
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val categories = setOf<String>()
    val sectio = mutableListOf<Pair<String, List<Product>>>()

    LaunchedEffect(Unit) {
        products = restaurantsViewModel.getProducts(restaurantsViewModel.restaurantSelected?.name!!)
    }
    products.forEach(){product ->
        if (!categories.contains(product.section)){
            categories.plus(product.section)
        }
    }
    categories.forEach(){category ->
        var sectionProducts = mutableListOf<Product>()
        products.forEach(){product ->
            if (product.section.equals(category)) sectionProducts.add(product)
        }
        sectio.add(Pair(category,sectionProducts))
    }

    val product = listOf<Pair<String,Float>>(Pair("Big Bubble", 3f), Pair("Sufflet", 2f), Pair("Gyoza", 1f))
    val product1 = listOf<Pair<String,Float>>(Pair("Bread", 2.5f), Pair("Cinnamon", 9f))
    val product2 = listOf<Pair<String,Float>>(Pair("Water", 0.5f))
    val sections = listOf<Pair<String, List<Pair<String,Float>>>>(Pair("Antipasti", product),Pair("Primi", product1),
                                                        Pair("Secondi", product2), Pair("Contorni", product),
                                                        Pair("Dolci", product),Pair("Bevande", product2))

    val isSurfaceOpen = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(16.dp),
                onClick = {} /*onAddButtonClicked*/
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_restaurant)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Text(
                    text = "My menù",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }

            //foreach per ogni portata
            sectio.forEach() {
                Text(
                    text = it.first,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                it.second.forEach() {
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearOutSlowInEasing
                                    )
                                )
                                .padding(15.dp, 2.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 1.dp)
                            ) {
                                Text(it.name!!)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("€ ${it.price}")
                                    IconButton(
                                        onClick = {isSurfaceOpen.value = true}
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.size(80.dp))

            if (isSurfaceOpen.value) {
                AlertDialog(
                    onDismissRequest = {
                        isSurfaceOpen.value = false
                    },
                ) {
                    DishEdit(
                        isSurfaceOpen,
                        restaurantsViewModel,
                        onConfirm = {
                            isSurfaceOpen.value = false // Aggiorna isSurfaceOpen quando viene premuto "Confirm"
                        }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishEdit(
    isSurfaceOpen: MutableState<Boolean>,
    restaurantsViewModel: RestaurantsViewModel,
    onConfirm: () -> Unit
) {
    val dishTypes = listOf("Antipasti", "Primi", "Secondi", "Dolci", "Bevande")
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var section by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Modifica pietanza:",
            )

            Box {
                dishTypes.forEach { type ->
                    AssistChip(
                        onClick = { section = type },
                        label = { Text(type) }
                    )
                }
            }

            var txtProduct by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(""))
            }
            OutlinedTextField(
                value = txtProduct,
                onValueChange = { txtProduct = it },
                label = { Text("Nome pietanza") }
            )

            var txtPrice by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(""))
            }
            OutlinedTextField(
                value = txtPrice,
                onValueChange = {
                    if (it.text.toDoubleOrNull() != null) {
                        txtPrice = it
                    } //val tmp = it.text.substring(0, it.text.indexOf(",") + 2)
                },
                label = { Text("Prezzo") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = {
                    isSurfaceOpen.value = false
                    onConfirm()

                    restaurantsViewModel.addNewProduct(
                        Product(
                            name = txtProduct.text,
                            description = description,
                            price = txtPrice.text,
                            section = section,
                        )
                    )
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(text = "Confirm")
            }
        }
    }
}

    /*Scaffold { paddingValues ->
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

            Button(
                onClick = {
                    restaurantsViewModel.addNewProduct(
                        Product(
                            name = name,
                            description = description,
                            price = price,
                            photo = photoURI
                        )
                    )
                    onNextButtonClicked()
                },
                colors = ButtonDefaults.buttonColors(Color.Green),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Text(text = stringResource(id = R.string.save))
            }

            if (capturedImageUri.path?.isNotEmpty() == true) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(capturedImageUri)
                        .crossfade(true)
                        .build(), contentDescription = "image taken"
                )

                photoURI = saveImage(context.applicationContext.contentResolver, capturedImageUri)
            }
        }
    }*/
