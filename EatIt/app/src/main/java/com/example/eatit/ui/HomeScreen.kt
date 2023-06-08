package com.example.eatit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.example.eatit.ui.components.EatItIconButton
import com.example.eatit.ui.components.EatItSearchBar
import com.example.eatit.ui.components.EatitFloatingButton
import com.example.eatit.ui.components.RestaurantCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    cartViewModel: CartViewModel,
    onFilterClicked: () -> Unit
) {
    if (Firebase.auth.currentUser == null) {
        onLoginClicked()
    }
    cartViewModel.resetOrder()
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var user by remember { mutableStateOf(User()) }
    LaunchedEffect(Unit) {
        usersViewModel.setUser(usersViewModel.getUser())
        user = usersViewModel.user
    }
    LaunchedEffect(user) {
        restaurants = if (user.restaurateur) {
            restaurantsViewModel.getRestaurantsByUserId(Firebase.auth.currentUser!!.uid)
        } else {
            restaurantsViewModel.getRestaurants()
        }
    }
    Scaffold(
        floatingActionButton = {
            if (user.restaurateur) {
                EatitFloatingButton(function = { onAddButtonClicked() }, icon = Icons.Filled.Add)
            }
        },
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            Row(modifier=modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EatItSearchBar(restaurants, onItemClicked, restaurantsViewModel)
                EatItIconButton(function = { onFilterClicked()}, icon = Icons.Filled.FilterList)
            }
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





