package com.example.eatit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.example.eatit.ui.components.AddRestaurantScreen
import com.example.eatit.ui.components.EatItFloatingButton
import com.example.eatit.ui.components.EatItIconButton
import com.example.eatit.ui.components.EatItSearchBar
import com.example.eatit.ui.components.RestaurantCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    cartViewModel: CartViewModel,
    onFilterClicked: () -> Unit,
    startLocationUpdates: () -> Unit
) {
    if (Firebase.auth.currentUser == null) {
        onLoginClicked()
    }

    val isNewRestaurant = remember { mutableStateOf(false) }
    cartViewModel.resetOrder()
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var user by remember { mutableStateOf(User()) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        usersViewModel.setUser(usersViewModel.getUser())
        user = usersViewModel.user
    }
    var restaurantsf by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    LaunchedEffect(user) {
        if (user.restaurateur) {
            restaurantsf =
                restaurantsViewModel.getRestaurantsByUserId(Firebase.auth.currentUser!!.uid)
        }
    }
    LaunchedEffect(usersViewModel.filter.favorite) {
        if (usersViewModel.filter.favorite) {
            restaurantsf =
                restaurantsViewModel.getRestaurantsByFavorite(Firebase.auth.currentUser!!.uid)
        } else {
            restaurantsf = restaurantsViewModel.getRestaurants()

        }
    }
    LaunchedEffect(restaurantsf) {
        if (!user.restaurateur) {
            restaurants = restaurantsf.filter { restaurant ->
                usersViewModel.filter.filterDistance(
                    restaurant,
                    user,
                    usersViewModel.filter.distance,
                    context
                )
            }
        } else {
            restaurants = restaurantsf
        }

        restaurants = usersViewModel.filter.sort(restaurants, usersViewModel.filter.sort)
    }



    Scaffold(
        floatingActionButton = {
            if (user.restaurateur) {
                EatItFloatingButton(
                    function = { isNewRestaurant.value = true },
                    icon = Icons.Filled.Add
                )
            }
        },
    ) { innerPadding ->
        if (isNewRestaurant.value) {
            AlertDialog(onDismissRequest = { isNewRestaurant.value = false }) {
                AddRestaurantScreen(
                    onNextButtonClicked = { isNewRestaurant.value = false },
                    restaurantsViewModel = restaurantsViewModel,
                    usersViewModel = usersViewModel,
                    startLocationUpdates = startLocationUpdates
                )
            }
        }
        Column(modifier.padding(innerPadding)) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EatItSearchBar(
                    restaurants,
                    onItemClicked,
                    restaurantsViewModel,
                    user,
                    usersViewModel
                )
                EatItIconButton(function = { onFilterClicked() }, icon = Icons.Filled.FilterList)
            }
            LazyColumn(Modifier.fillMaxWidth()) {
                items(restaurants.size) { index ->
                    RestaurantCard(
                        restaurants[index],
                        onItemClicked,
                        restaurantsViewModel,
                        user,
                        usersViewModel
                    )
                }
            }
        }
    }
}





