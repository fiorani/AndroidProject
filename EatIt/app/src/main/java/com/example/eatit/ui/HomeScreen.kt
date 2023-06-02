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

@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier
) {
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
            ristorantList(onItemClicked, restaurantsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ristorantList(onItemClicked: () -> Unit, restaurantsViewModel: RestaurantsViewModel) {
    val db = FirebaseFirestore.getInstance()
    val restaurantsCollection = db.collection("restaurants")
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val restaurants = remember { mutableStateListOf<DocumentSnapshot>() }
    restaurantsCollection.get()
        .addOnSuccessListener { querySnapshot ->
            restaurants.addAll(querySnapshot.documents)
        }
        .addOnFailureListener { exception ->
            println("Error getting restaurants: $exception")
        }
    Column {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            var searchResults = remember { mutableStateListOf<DocumentSnapshot>() }
            restaurantsCollection.whereEqualTo("name", query).get()
                .addOnSuccessListener { querySnapshot ->
                    searchResults.addAll(querySnapshot.documents)
                }
                .addOnFailureListener { exception ->
                    println("Error getting restaurants: $exception")
                }
            LazyColumn {
                items(searchResults.size) { index ->
                    val restaurant = searchResults[index]
                    ristorantCard(restaurant, onItemClicked, restaurantsViewModel)
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(restaurants.size) { index ->
            val restaurant = restaurants[index]
            ristorantCard(restaurant, onItemClicked, restaurantsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ristorantCard(
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            Text(
                text = restaurant.data!!["name"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            Text(
                text = restaurant.data!!["city"].toString(),
                modifier = Modifier.padding(8.dp),
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
                }
            )

        }
    }
}



