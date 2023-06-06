package com.example.eatit.ui

import android.util.Log
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
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.ui.components.ProductCard
import com.example.eatit.ui.components.RatingCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit,
    cartViewModel: CartViewModel
) {
    val restaurant = restaurantsViewModel.restaurantSelected
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var ratings by remember { mutableStateOf<List<Rating>>(emptyList()) }
    LaunchedEffect(Unit) {
        Log.d("DetailsRestaurantScreen", "restaurant: $restaurant")
        products=restaurantsViewModel.getProducts(restaurant?.id.toString())
        Log.d("DetailsRestaurantScreen", "products: $products")
        ratings=restaurantsViewModel.getRatings(restaurant?.id.toString())
    }
    cartViewModel.selectOrder(
        Order(
            userId = Firebase.auth.currentUser?.uid.toString(),
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
        )
    )
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
                ImageProfile(restaurant?.photo.toString())
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .height(200.dp)
                ) {
                    Text(
                        text = restaurant?.name.toString(),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.titleLarge,

                        )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = restaurant?.city.toString(),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.size(15.dp))

            LazyColumn {
                items(products.size) { index ->
                    ProductCard(products[index], cartViewModel)
                }
                items(ratings.size) { index ->
                    RatingCard(ratings[index])
                }
            }
        }
    }
}

