package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.EatItImageProfile
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
    val user = remember { usersViewModel.getUser() }
    val orders = remember { cartViewModel.getOrders() }
    Scaffold { innerPadding ->
        BackgroundImage(0.05f)
        Column(modifier.padding(innerPadding)) {
            if (user.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    EatItImageProfile(user[0].data?.get("photo").toString())
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .height(200.dp)
                    ) {
                        Text(
                            text = user[0].data!!["userName"].toString(),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.titleLarge,

                            )
                        Spacer(modifier = Modifier.size(15.dp))
                        Text(
                            text = user[0].data!!["address"].toString(),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }
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
                    var restaurant by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
                    LaunchedEffect(Unit) {
                        restaurant = restaurantsViewModel.getRestaurant(
                            orders[item].data?.get("restaurantId").toString()
                        )
                    }
                    OrderCard(
                        orders[item],
                        products,
                        restaurant
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
    orders: DocumentSnapshot,
    listProducts: List<DocumentSnapshot>,
    restaurant: List<DocumentSnapshot>
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
            if (restaurant.isNotEmpty()) {
                Text(
                    text = restaurant[0].data?.get("name").toString(),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 32.sp
                )

                Text(
                    text = "total price:"+orders.data?.get("totalPrice").toString(),
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (expandedState) {
                Column(Modifier.fillMaxWidth()) {
                    listProducts.forEachIndexed { index, product ->
                        Text(
                            text = product.data?.get("name").toString()+" price="+product.data?.get("price").toString()+"*"+(orders.data?.get("listQuantity") as List<String>)[index],
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
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


