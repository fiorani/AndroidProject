package com.example.eatit.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
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
import java.time.ZoneId
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
    val giorni by remember { mutableStateOf(mutableListOf<Int>()) }
    for (i in 0 until 30) {
        giorni.add(0)
    }
    LaunchedEffect(Unit) {
        orders = if (user.restaurateur) {
            cartViewModel.getOrdersRestaurateur()
        } else {
            cartViewModel.getOrders()
        }
        Log.d("ORDERS", orders.toString())
        for (order in orders) {
            val calendar = Calendar.getInstance()
            calendar.time = order.timestamp
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            if (day in 1..30) {
                giorni[day - 1] += 1
            }
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
                        ImageProfile(user.photo)
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .height(200.dp)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.size(15.dp))
                            Text(
                                text = user.position,
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
                            fontWeight = Bold
                        )
                        // Creazione del modello dei dati per il grafico
                        val chartEntryModel = entryModelOf(
                            giorni[0].toFloat(), giorni[1].toFloat(), giorni[2].toFloat(), giorni[3].toFloat(), giorni[4].toFloat(), giorni[5].toFloat(),
                            giorni[6].toFloat(), giorni[7].toFloat(), giorni[8].toFloat(), giorni[9].toFloat(), giorni[10].toFloat(), giorni[11].toFloat(),
                            giorni[12].toFloat(), giorni[13].toFloat(), giorni[14].toFloat(), giorni[15].toFloat(), giorni[16].toFloat(), giorni[17].toFloat(),
                            giorni[18].toFloat(), giorni[19].toFloat(), giorni[20].toFloat(), giorni[21].toFloat(), giorni[22].toFloat(), giorni[23].toFloat(),
                            giorni[24].toFloat(), giorni[25].toFloat(), giorni[26].toFloat(), giorni[27].toFloat(), giorni[28].toFloat(), giorni[29].toFloat()
                        )
                        Chart(
                            chart = lineChart(),
                            model = chartEntryModel,
                            startAxis = startAxis(),
                            bottomAxis = bottomAxis()
                        )
                        Text(
                            text = "Orders:",
                            modifier = Modifier.padding(20.dp, 10.dp),
                            fontSize = 32.sp,
                            fontWeight = Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCarouselCard(badgesList: List<Triple<Int, String, String>>) {
    val showBadgeDesc = remember { mutableStateOf(false) }
    var badgeTitle = ""
    var badgeDesc = ""
    
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
                for (badge in badgesList) {
                    Image(
                        painter = painterResource(badge.first),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(80.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .clickable {
                                showBadgeDesc.value = true
                                badgeTitle = badge.second
                                badgeDesc = badge.third
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        if (showBadgeDesc.value) {
            AlertDialog(onDismissRequest = { showBadgeDesc.value = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 40.dp, 20.dp, 10.dp),
                        text = badgeTitle,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = Bold
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 0.dp, 20.dp, 10.dp),
                        text = badgeDesc,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            modifier = Modifier.padding(10.dp),
                            onClick = { showBadgeDesc.value = false }
                        ) {
                            Text(
                                text = "Close",
                                fontSize = 20.sp,
                                fontStyle = Italic
                            )
                        }
                    }
                }
            }
        }

    }
}

fun getBadges(orderNum: Int) : List<Triple<Int, String, String>> {
    val result = mutableListOf(
        Triple(R.drawable.badge_login, "First login!", "Login for the first time.")
    )
    val badgeMap = mapOf(
        Pair(1, Triple(R.drawable.badge1, "First Order!", "Congratulations! You have made your first order.")),
        Pair(10, Triple(R.drawable.badge10, "10 Orders", "You have made 10 orders!")),
        Pair(20, Triple(R.drawable.badge20, "20 Orders", "You have made 20 orders!")),
        Pair(30, Triple(R.drawable.badge30, "30 Orders", "You have made 30 orders!")),
        Pair(40, Triple(R.drawable.badge40, "40 Orders", "You have made 40 orders!")),
        Pair(50, Triple(R.drawable.badge50, "50 Orders", "You have made 50 orders!")),
        Pair(60, Triple(R.drawable.badge_max, "EatIt Master", "Amazing! You have made a lot of orders!"))
    )

    badgeMap.forEach{level ->
        if (orderNum >= level.key) {
            result.add(level.value)
        }
    }

    return result
}