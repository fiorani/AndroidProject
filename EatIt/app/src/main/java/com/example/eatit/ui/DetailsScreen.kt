package com.example.eatit.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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

@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit
) {
    val restaurant = restaurantsViewModel.restaurantSelected

    val db = FirebaseFirestore.getInstance()
    val restaurantCollection = db.collection("restaurants").document(restaurant?.id.toString())

    val productCollection = restaurantCollection.collection("products")
    val products = remember { mutableStateListOf<DocumentSnapshot>() }
    productCollection.get().addOnSuccessListener { querySnapshot ->
        products.addAll(querySnapshot.documents)
    }.addOnFailureListener { exception ->
        println("Error getting restaurants: $exception")
    }

    val ratingsCollection = restaurantCollection.collection("ratings")
    val ratings = remember { mutableStateListOf<DocumentSnapshot>() }
    ratingsCollection.get().addOnSuccessListener { querySnapshot ->
        ratings.addAll(querySnapshot.documents)
    }.addOnFailureListener { exception ->
        println("Error getting restaurants: $exception")
    }

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
            modifier = Modifier.padding(paddingValues)
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

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Log.d("TAG", "entrato:")
                items(products.size) { index ->
                    val product = products[index]
                    productCard(product)
                }
                items(ratings.size) { index ->
                    val rating = ratings[index]
                    ratingCard(rating)
                }
            }
        }
    }
}

@Composable
fun productCard(
    product: DocumentSnapshot
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

@Composable
fun ratingCard(
    rating: DocumentSnapshot
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