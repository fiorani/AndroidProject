package com.example.eatit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eatit.R
import com.example.eatit.model.Orders
import com.example.eatit.ui.components.EatItImage
import com.example.eatit.ui.components.ProductCard
import com.example.eatit.ui.components.RatingCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot

@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit,
            cartViewModel: CartViewModel
) {
    val restaurant = restaurantsViewModel.restaurantSelected
    val productCollection = restaurantsViewModel.getProducts(restaurant?.id.toString())
    val products = remember { mutableStateListOf<DocumentSnapshot>() }
    products.clear()
    productCollection.addOnSuccessListener { querySnapshot ->
        products.addAll(querySnapshot.documents)
    }.addOnFailureListener { exception ->
        println("Error getting restaurants: $exception")
    }

    val ratingsCollection =restaurantsViewModel.getRatings(restaurant?.id.toString())
    val ratings = remember { mutableStateListOf<DocumentSnapshot>() }
    ratings.clear()
    ratingsCollection.addOnSuccessListener { querySnapshot ->
        ratings.addAll(querySnapshot.documents)
    }.addOnFailureListener { exception ->
        println("Error getting restaurants: $exception")
    }
    cartViewModel.selectOrder(Orders(
    userId = "1",
    restaurantId = restaurant?.id.toString(),
        listProductId = ArrayList(
            listOf()
        ),
        listQuantity = ArrayList(
            listOf()
        ),
        listPrice = ArrayList(
            listOf()
        ),
        totalPrice = 0.0,
    ))
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
            Box(modifier = Modifier.fillMaxWidth()) {
                EatItImage(restaurant?.photo ?: "")
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .height(200.dp)) {
                    Text(
                        text = restaurant?.name ?: stringResource(id = R.string.restaurant_name),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.titleLarge,

                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = restaurant?.city ?: stringResource(id = R.string.restaurant_description),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.size(15.dp))

            LazyColumn {
                items(products.size) { index ->
                    val product = products[index]
                    ProductCard(product,cartViewModel)
                }
                items(ratings.size) { index ->
                    val rating = ratings[index]
                    RatingCard(rating)
                }
            }
        }
    }
}

