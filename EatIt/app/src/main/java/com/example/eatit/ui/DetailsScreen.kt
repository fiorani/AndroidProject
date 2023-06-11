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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.dp
import com.example.eatit.R
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.ui.components.AddProductScreen
import com.example.eatit.ui.components.CancelDialog
import com.example.eatit.ui.components.EatItButton
import com.example.eatit.ui.components.EatItFloatingButton
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.ui.components.SectionMenuCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRestaurantScreen(
    restaurantsViewModel: RestaurantsViewModel,
    //onAddButtonClicked: () -> Unit,
    cartViewModel: CartViewModel,
    usersViewModel: UsersViewModel,
    onNextButtonClicked: () -> Unit,
) {
    restaurantsViewModel.resetProduct()

    val restaurant = restaurantsViewModel.restaurantSelected
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var ratings by remember { mutableStateOf<List<Rating>>(emptyList()) }
    var order by remember { mutableStateOf<Order?>(Order()) }
    val user = usersViewModel.user

    val isAdding = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        products = restaurantsViewModel.getProducts(restaurant.id!!)
        ratings = restaurantsViewModel.getRatings(restaurant.id!!)
    }
    if (cartViewModel.orderSelected.id != "") {
        order = cartViewModel.orderSelected
    } else {
        order = Order(
            userId = Firebase.auth.currentUser?.uid.toString(),
            restaurantId = restaurant.id.toString(),
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
                EatItFloatingButton(function = { isAdding.value = true }, icon = Icons.Filled.Add)
            } else {
                EatItFloatingButton(
                    function = {
                        cartViewModel.selectOrder(order!!)
                        onNextButtonClicked()
                    },
                    icon = Icons.Filled.ShoppingCart
                )
            }
        },
    ) { paddingValues ->
        restaurantsViewModel.resetProduct()
        if (isAdding.value) {
            restaurantsViewModel.resetProduct()
            AlertDialog(onDismissRequest = { isAdding.value = false }) {
                AddProductScreen(
                    onNextButtonClicked = { isAdding.value = false
                                          },
                    restaurantsViewModel = restaurantsViewModel
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ImageProfile(restaurant.photo)
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Top
                    ) {
                        val isDeleting = remember { mutableStateOf(false) }

                        IconButton(onClick = { isDeleting.value = true })
                        {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete restaurant",
                                tint = MaterialTheme.colorScheme.background
                            )
                        }

                        if (isDeleting.value) {
                            CancelDialog(
                                onDismissRequest = { isDeleting.value = false },
                                text = "Are you sure you want to delete this restaurant?",
                                cancellingQuery = {


                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        val context = LocalContext.current
                        EatItButton(text = "GO ", function = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${restaurant.address}")
                            )
                            context.startActivity(intent)
                        }, icon = Icons.Filled.Map)
                        Spacer(modifier = Modifier.size(15.dp))
                        EatItButton(text = "call", function = {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:${restaurant.phone}")
                            context.startActivity(intent)
                        }, icon = Icons.Filled.Call)
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
                        //onAddButtonClicked = onAddButtonClicked
                    )
                }
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}


