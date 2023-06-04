package com.example.eatit.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.ui.components.EatItimage
import com.example.eatit.ui.components.ProductCard
import com.example.eatit.ui.components.RatingCard
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit
) {
    val restaurant = restaurantsViewModel.restaurantSelected

    val restaurantCollection = FirebaseFirestore.getInstance().collection("restaurants")
        .document(restaurant?.id.toString())

    val productCollection = restaurantCollection.collection("products")
    val products = remember { mutableStateListOf<DocumentSnapshot>() }
    products.clear()
    productCollection.get().addOnSuccessListener { querySnapshot ->
        products.addAll(querySnapshot.documents)
    }.addOnFailureListener { exception ->
        println("Error getting restaurants: $exception")
    }

    val ratingsCollection = restaurantCollection.collection("ratings")
    val ratings = remember { mutableStateListOf<DocumentSnapshot>() }
    ratings.clear()
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
            modifier = Modifier
                .padding(paddingValues)
        ) {
            Surface(modifier = Modifier.size(200.dp)) {
                EatItimage(restaurant?.photo ?: "")
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

            LazyColumn {
                items(products.size) { index ->
                    val product = products[index]
                    ProductCard(product)
                }
                items(ratings.size) { index ->
                    val rating = ratings[index]
                    RatingCard(rating)
                }
            }
        }
    }
}

