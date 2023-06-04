package com.example.eatit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.eatit.R
import com.example.eatit.ui.components.EatItSearchBar
import com.example.eatit.ui.components.RestaurantCard
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier,
) {
    val restaurantsCollection = FirebaseFirestore.getInstance().collection("restaurants").get()
    val restaurants = remember { mutableStateListOf<DocumentSnapshot>() }
    restaurants.clear()
    restaurantsCollection.addOnSuccessListener { querySnapshot ->
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
            EatItSearchBar(
                restaurantsCollection, onItemClicked,
                restaurantsViewModel
            )
            LazyColumn(Modifier.fillMaxWidth()) {
                items(restaurants.size) { index ->
                    val restaurant = restaurants[index]
                    RestaurantCard(
                        restaurant,
                        onItemClicked,
                        restaurantsViewModel
                    )
                }
            }
        }
    }
}





