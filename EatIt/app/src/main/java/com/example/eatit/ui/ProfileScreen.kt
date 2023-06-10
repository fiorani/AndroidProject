package com.example.eatit.ui

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.ImageProfile
import com.example.eatit.ui.components.OrderProfileCard
import com.example.eatit.viewModel.CartViewModel
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel
) {
    val user: User = usersViewModel.user
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    LaunchedEffect(Unit) {
        orders = if (user.restaurateur) {
            cartViewModel.getOrdersRestaurateur()
        } else {
            cartViewModel.getOrders()
        }
        Log.d("ORDERS", orders.toString())
    }

    Scaffold { innerPadding ->
        BackgroundImage(0.05f)
        Column(modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ImageProfile(user.photo.toString())
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .height(200.dp)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = user.name.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                            Text(
                                text = user.position.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                        }
                    }

                    if (!user.restaurateur) {
                        Text(
                            modifier = Modifier.padding(20.dp, 10.dp),
                            text = "Achievements",
                            fontWeight = Bold,
                            fontSize = 32.sp
                        )
                        ImageCarouselCard(getBadges(orders.size))
                        Text(
                            text = "My orders",
                            modifier = Modifier.padding(20.dp, 10.dp),
                            fontSize = 32.sp,
                            fontWeight = Bold
                        )
                    } else {
                        Text(
                            text = "Order statistics:",
                            modifier = Modifier.padding(20.dp, 10.dp),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val chartEntryModel = entryModelOf(4f, 12f, 8f, 16f)
                        Chart(
                            chart = lineChart(),
                            model = chartEntryModel,
                            startAxis = startAxis(),
                            bottomAxis = bottomAxis(),
                        )
                        Text(
                            text = "Orders:",
                            modifier = Modifier.padding(20.dp, 10.dp),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                items(orders.size) { item ->
                    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
                    var restaurant by remember { mutableStateOf(Restaurant()) }
                    LaunchedEffect(Unit) {
                        products = cartViewModel.getProducts(orders[item])
                        restaurant = restaurantsViewModel.getRestaurant(
                            orders[item].restaurantId
                        )
                    }
                    OrderProfileCard(
                        orders[item],
                        products,
                        restaurant,
                        user,
                        cartViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ImageCarouselCard(badgesList: List<Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 10.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                for (imageResId in badgesList) {
                    Image(
                        painter = painterResource(imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(80.dp)
                            .clip(shape = RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

fun getBadges(orderNum: Int) : List<Int> {
    val result = mutableListOf<Int>()
    val badgeMap = mapOf<Int, Int>(
        Pair(1, R.drawable.badge1),
        Pair(10, R.drawable.badge10),
        Pair(20, R.drawable.badge20),
        Pair(30, R.drawable.badge30),
        Pair(40, R.drawable.badge40),
        Pair(50, R.drawable.badge50),
        Pair(60, R.drawable.badge_max)
    )

    badgeMap.forEach(){level ->
        if (orderNum >= level.key) {
            result.add(level.value)
        }
    }

    return result
}