package com.example.eatit.ui

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.model.User
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.ui.components.ProductCard
import com.example.eatit.ui.components.QuantitySelector
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onAddButtonClicked: () -> Unit,
    cartViewModel: CartViewModel,
    usersViewModel: UsersViewModel,
    onNextButtonClicked: () -> Unit,
) {
    val restaurant = restaurantsViewModel.restaurantSelected
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var ratings by remember { mutableStateOf<List<Rating>>(emptyList()) }
    val user = usersViewModel.user!!
    LaunchedEffect(Unit) {
        products = restaurantsViewModel.getProducts(restaurant?.id.toString())
        ratings = restaurantsViewModel.getRatings(restaurant?.id.toString())
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
            if (user.restaurateur) {
                FloatingActionButton(onClick = onAddButtonClicked) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_restaurant)
                    )
                }
            }else{
                FloatingActionButton(onClick = onNextButtonClicked) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = stringResource(id = R.string.add_restaurant)
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ImageProfile(restaurant?.photo.toString())
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.height(200.dp)
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
                LocalContext.current.resources.getStringArray(R.array.categories)
                    .forEach { category ->
                        SectionCard(
                            sectionName = category.toString(),
                            products = products,
                            cartViewModel = cartViewModel,
                            user = user
                        )
                    }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionCard(
    sectionName: String, products: List<Product>, cartViewModel: CartViewModel, user: User
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp, 10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,
        onClick = {
            expandedState = !expandedState
        }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(20.dp, 10.dp),
                text = sectionName,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(modifier = Modifier.rotate(rotationState), onClick = {
                expandedState = !expandedState
            }) {
                Icon(
                    imageVector = Icons.Default.ExpandMore, contentDescription = "Drop-Down Arrow"
                )
            }
        }
    }

    if (expandedState) {
        products.forEach { product ->
            ProductCard(
                product = product,
                cartViewModel = cartViewModel,
                user = user
            )
        }

    }

}