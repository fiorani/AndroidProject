package com.example.eatit.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit
) {
    val context = LocalContext.current
    val restaurant = restaurantsViewModel.restaurantSelected
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddButtonClicked) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_restaurant)
                )
            }
        },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            if (restaurant?.photo != "") {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(restaurant?.photo))
                        .crossfade(true)
                        .build(),
                    contentDescription = "image of the restaurant",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = restaurant?.name ?: stringResource(id = R.string.restaurant_name),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = restaurant?.city ?: stringResource(id = R.string.restaurant_description),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            val db = FirebaseFirestore.getInstance()

            val productCollection = db.collection("restaurants").document(restaurant?.id.toString())
                .collection("products")
            val products = remember { mutableStateListOf<DocumentSnapshot>() }
            productCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    products.addAll(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    println("Error getting restaurants: $exception")
                }

            val ratingsCollection = db.collection("restaurants").document(restaurant?.id.toString())
                .collection("ratings")
            val ratings = remember { mutableStateListOf<DocumentSnapshot>() }
            ratingsCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    ratings.addAll(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    println("Error getting restaurants: $exception")
                }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(products.size) { index ->
                    val product = products[index]
                    productCard(product, restaurantsViewModel)
                }
                items(ratings.size) { index ->
                    val rating = ratings[index]
                    ratingCard(rating, restaurantsViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun productCard(
    product: DocumentSnapshot,
    restaurantsViewModel: RestaurantsViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = product.data!!["name"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp
            )
            Text(
                text = product.data!!["description"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 15.sp
            )
            Text(
                text = product.data!!["price"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 15.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ratingCard(
    rating: DocumentSnapshot,
    restaurantsViewModel: RestaurantsViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = rating.data!!["text"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp
            )
            Text(
                text = rating.data!!["userName"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 15.sp
            )
            var valrating: Float by remember { mutableStateOf(3.2f) }
            RatingBar(
                value = rating.data!!["rating"].toString().toFloat(),
                style = RatingBarStyle.Fill(),
                onValueChange = {
                    valrating = it
                },
                onRatingChanged = {
                    Log.d("TAG", "onRatingChanged: $it")
                }
            )

        }
    }
}