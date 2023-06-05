package com.example.eatit.ui

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.model.Restaurant
import com.example.eatit.ui.components.EatItImage
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
        Column(modifier.padding(innerPadding)) {
            if (user.size > 0) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    EatItImage(user[0].data?.get("photo").toString())
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
                            text = user[0].data!!["userName"].toString(),
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                }
            }

            Text(
                text = "Order list:",
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(orders.size) { item ->
                    OrderCard(
                        orders[item],
                        restaurantsViewModel,
                        cartViewModel,
                        cartViewModel.getProducts(orders[item])
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
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel,
    listProducts: List<DocumentSnapshot>
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    var restaurant: Restaurant? = null
    restaurantsViewModel.getRestaurant(orders.data?.get("restaurantId").toString())
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                restaurant = documentSnapshot.toObject(Restaurant::class.java)
            }
        }
        .addOnFailureListener { exception ->
            // Gestisci l'eventuale errore nell'ottenimento del documento
        }

    Log.d("listProducts", listProducts.size.toString())
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
                text = restaurant?.name.toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            Text(
                text = orders.data?.get("totalPrice").toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
            if (expandedState) {
                Column(Modifier.fillMaxWidth()) {
                    listProducts.forEachIndexed { index, product ->
                        Text(
                            text = product.data?.get("name").toString(),
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                        Text(
                            text = (orders.data?.get("listQuantity") as List<String>)[index],
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


