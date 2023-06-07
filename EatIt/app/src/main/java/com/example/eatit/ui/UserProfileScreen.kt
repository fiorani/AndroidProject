package com.example.eatit.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    usersViewModel: UsersViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel
) {
    val user: User = usersViewModel.user!!
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    LaunchedEffect(Unit) {
        if (user.restaurateur) {
            orders = cartViewModel.getOrders()
        } else {
            orders = cartViewModel.getOrders()
        }
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
                    Text(
                        text = "Statistic order:",
                        modifier = Modifier.padding(20.dp, 10.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if(!user.restaurateur)
                    {
                        ImageCarouselCard()
                    }else{
                        val chartEntryModel = entryModelOf(4f, 12f, 8f, 16f)
                        Chart(
                            chart = lineChart(),
                            model = chartEntryModel,
                            startAxis = startAxis(),
                            bottomAxis = bottomAxis(),
                        )
                    }

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

@Composable
fun ImageCarouselCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Achievement",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                val imageList = listOf(
                    R.drawable.medal1,
                    R.drawable.medal2,
                    R.drawable.medal3,
                    R.drawable.medal4,
                    R.drawable.medal5,
                    R.drawable.medal6,
                    R.drawable.medal7,
                    R.drawable.medal8,
                    R.drawable.medal9,
                    R.drawable.medal10,
                    R.drawable.medal11
                )

                for (imageResId in imageList) {
                    Image(
                        painter = painterResource(imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(50.dp)
                            .clip(shape = RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}