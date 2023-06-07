package com.example.eatit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel
) {
    val user: User = usersViewModel.user!!
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    LaunchedEffect(Unit) {
        if (user.restaurateur){
            orders = cartViewModel.getOrders()
        }else{
            orders = cartViewModel.getOrders()
        }
    }

    Scaffold { innerPadding ->
        BackgroundImage(0.05f)
        Column(modifier.padding(innerPadding)) {
            val chartEntryModel = entryModelOf(4f, 12f, 8f, 16f)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                item{
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ImageProfile(user.photo.toString())
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .height(200.dp)
                        ) {
                            Text(
                                text = user.name.toString(),
                                style = MaterialTheme.typography.titleLarge,

                                )
                            Spacer(modifier = Modifier.size(15.dp))
                            Text(
                                text = user.position.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                        }
                    }
                    Text(
                        text = "Statistic order:",
                        modifier = Modifier.padding(20.dp, 10.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Chart(
                        chart = lineChart(),
                        model = chartEntryModel,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                    Text(
                        text = "My orders:",
                        modifier = Modifier.padding(20.dp, 10.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(orders.size) { item ->
                    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
                    var restaurant by remember { mutableStateOf(Restaurant()) }
                    LaunchedEffect(Unit) {
                        products = cartViewModel.getProducts(orders[item])
                        restaurant = restaurantsViewModel.getRestaurant(
                            orders[item].restaurantId.toString()
                        )
                    }
                    OrderProfileCard(
                        orders[item],
                        products,
                        restaurant,
                    )
                }
            }
        }
    }
}