package com.example.eatit.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.model.Restaurant
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier
) {
    val restaurantsCollection = FirebaseFirestore.getInstance().collection("restaurants")
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val restaurants = remember { mutableStateListOf<DocumentSnapshot>() }
    restaurants.clear()
    restaurantsCollection.get()
        .addOnSuccessListener { querySnapshot ->
            restaurants.addAll(querySnapshot.documents)
        }
        .addOnFailureListener { exception ->
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
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { active = false },
                active = active,
                onActiveChange = { active = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                var searchResults = remember { mutableStateListOf<DocumentSnapshot>() }
                searchResults.clear()
                restaurantsCollection.get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot)
                        {
                            if (document.data?.get("name").toString()
                                    .contains(query, ignoreCase = true)
                            ) {
                                searchResults.add(document)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Error getting restaurants: $exception")
                    }
                LazyColumn {
                    items(searchResults.size) { index ->
                        val restaurant = searchResults[index]
                        RestaurantCard(restaurant, onItemClicked, restaurantsViewModel)
                    }
                }
            }
            LazyColumn() {
                items(restaurants.size) { index ->
                    val restaurant = restaurants[index]
                    RestaurantCard(restaurant, onItemClicked, restaurantsViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCard(
    restaurant: DocumentSnapshot,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel
) {
    Card(
        onClick = {
            val restaurantt = restaurant.toObject(Restaurant::class.java)
            restaurantt?.id = restaurant.id
            restaurantsViewModel.selectRestaurant(restaurantt!!)
            onItemClicked()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,

        ) {
        Row(modifier = Modifier.fillMaxWidth()){
            if (restaurant.data?.get("photo").toString() != "") {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(restaurant.data?.get("photo").toString()))
                        .crossfade(true)
                        .build(),
                    contentDescription = "image of the restaurant",
                    modifier = Modifier
                        .size(size = 100.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = restaurant.data!!["name"].toString(),
                    modifier = Modifier.padding(4.dp),
                    fontSize = 28.sp
                )
                Text(
                    text = restaurant.data!!["city"].toString(),
                    modifier = Modifier.padding(4.dp),
                    fontSize = 16.sp
                )
                var rating: Float by remember { mutableStateOf(3.2f) }
                RatingBar(
                    value = restaurant.data!!["avgRating"].toString().toFloat(),
                    style = RatingBarStyle.Fill(),
                    onValueChange = {
                        rating = it
                    },
                    onRatingChanged = {
                        Log.d("TAG", "onRatingChanged: $it")
                    },
                    modifier = Modifier.padding(4.dp),
                    size = 20.dp
                )

            }
        }

    }
}



