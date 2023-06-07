package com.example.eatit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eatit.R
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.example.eatit.ui.components.EatItSearchBar
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
        user = usersViewModel.user!!
        usersViewModel.setPosition(user.position.toString())
        restaurants = if (user.restaurateur) {
            restaurantsViewModel.getRestaurantsByUserId(Firebase.auth.currentUser!!.uid)
        } else {
            restaurantsViewModel.getRestaurants()
        }
    }
    Scaffold(
        floatingActionButton = {
            if (user.restaurateur) {
                FloatingActionButton(
                    shape = RoundedCornerShape(20.dp),
                    onClick = onAddButtonClicked
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_restaurant)
                    )
                    /*Text(
                        text = "Modify menù",
                        modifier = Modifier.padding(20.dp),
                        fontWeight = Bold,
                        fontSize = 20.sp
                    )*/
                }
            }
        },
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            Row(modifier.fillMaxWidth()) {
                EatItSearchBar(
                    restaurants, onItemClicked,
                    restaurantsViewModel
                )
                IconButton(onClick = onFilterClicked,
                modifier = Modifier.align(Alignment.CenterVertically)) {
                    Icon(
                        Icons.Filled.FilterList,
                        contentDescription = "Filter"
                    )
                }
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





