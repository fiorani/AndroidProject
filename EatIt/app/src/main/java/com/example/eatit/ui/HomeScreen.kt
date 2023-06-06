package com.example.eatit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eatit.R
import com.example.eatit.ui.components.EatItSearchBar
import com.example.eatit.ui.components.RestaurantCard
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (Firebase.auth.currentUser == null) {
        onLoginClicked()
    }
    var restaurants by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    LaunchedEffect(Unit) {
        restaurants = restaurantsViewModel.getRestaurants()
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = RoundedCornerShape(16.dp),
                onClick = onAddButtonClicked
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_restaurant)
                )
            }
        },
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            EatItSearchBar(
                restaurants, onItemClicked,
                restaurantsViewModel
            )
            LazyColumn(Modifier.fillMaxWidth()) {
                items(restaurants.size) { index ->
                    RestaurantCard(
                        restaurants[index],
                        onItemClicked,
                        restaurantsViewModel
                    )
                }
            }
        }
    }
}





