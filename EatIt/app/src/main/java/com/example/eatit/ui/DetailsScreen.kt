package com.example.eatit.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eatit.R
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.ui.components.SectionMenuCard
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
    restaurantsViewModel.resetProduct()

    val restaurant = restaurantsViewModel.restaurantSelected
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var ratings by remember { mutableStateOf<List<Rating>>(emptyList()) }
    var order by remember { mutableStateOf<Order?>(null) }
    val user = usersViewModel.user!!

    LaunchedEffect(Unit) {
        products = restaurantsViewModel.getProducts(restaurant?.id.toString())
        ratings = restaurantsViewModel.getRatings(restaurant?.id.toString())
    }
    if (cartViewModel.orderSelected != null) {
        order = cartViewModel.orderSelected
    } else {
        order = Order(
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
            totalPrice = 0.0f,
        )
    }

    Scaffold(
        floatingActionButton = {
            if (user.restaurateur) {
                FloatingActionButton(shape = RoundedCornerShape(25.dp),onClick = onAddButtonClicked) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add_restaurant)
                    )
                }
            } else {

                FloatingActionButton(onClick =
                {
                    cartViewModel.selectOrder(order!!)
                    onNextButtonClicked()
                }) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ImageProfile(restaurant?.photo.toString())
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.height(200.dp).fillMaxWidth()
                ) {
                    Text(
                        text = restaurant?.name.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                        )
                    Spacer(modifier = Modifier.size(15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,) {
                        val context = LocalContext.current
                        Button(onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${restaurant?.address}")
                            )
                            context.startActivity(intent)
                        }) {
                            Text(
                                text = "GO "+restaurant?.address.toString(),
                            )
                            Icon(
                                Icons.Filled.Map,
                                contentDescription = "go"
                            )
                        }
                        Spacer(modifier = Modifier.size(15.dp))
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${restaurant?.phone}")
                            context.startActivity(intent)
                        }) {
                            Text(
                                text = "CALL ",
                            )
                            Icon(
                                Icons.Filled.Call,
                                contentDescription = "call"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(15.dp))

                }
            }
            LocalContext.current.resources.getStringArray(R.array.categories)
                .forEach { category ->
                    SectionMenuCard(
                        sectionName = category.toString(),
                        products = products,
                        restaurantViewModel = restaurantsViewModel,
                        order = order!!,
                        user = user,
                        onAddButtonClicked
                    )
                }

        }
    }
}


