package com.example.eatit.ui

import android.graphics.Paint.Align
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.firestore.DocumentSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel
) {
    var user: User by remember { mutableStateOf(User()) }
    val orders = remember { cartViewModel.getOrders() }
    LaunchedEffect(Unit) {
        user = usersViewModel.getUser()
    }
    Scaffold { innerPadding ->
        BackgroundImage(0.05f)
        Column(modifier.padding(innerPadding)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ImageProfile(user.photo.toString())
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .height(200.dp)
                    ) {
                        Text(
                            text = user.userName.toString(),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.titleLarge,

                            )
                        Spacer(modifier = Modifier.size(15.dp))
                        Text(
                            text = user.address.toString(),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                }


            Text(
                text = "List of orders:",
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(orders.size) { item ->
                    val products = remember { cartViewModel.getProducts(orders[item]) }
                    var restaurant by remember { mutableStateOf(Restaurant()) }
                    LaunchedEffect(Unit) {
                        restaurant = restaurantsViewModel.getRestaurant(
                            orders[item].data?.get("restaurantId").toString()
                        )
                    }
                    OrderCard1(
                        orders[item],
                        products,
                        restaurant,
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard1(
    orders: DocumentSnapshot,
    listProducts: List<DocumentSnapshot>,
    restaurant: Restaurant
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(8.dp),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
                Text(
                    text = restaurant.name.toString(),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 32.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Total price:",
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "€" + String.format("%.${2}f", orders.data?.get("totalPrice")),
                        modifier = Modifier.padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }


            if (expandedState) {
                Column(Modifier.fillMaxWidth()) {
                    listProducts.forEachIndexed { index, product ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = product.data?.get("name").toString(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "€ " + product.data?.get("price")
                                    .toString() + " x" + (orders.data?.get("listQuantity") as List<String>)[index],
                                modifier = Modifier.padding(8.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                    Text(
                        text = "12/03/23",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
        }
    }
}