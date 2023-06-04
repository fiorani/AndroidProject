package com.example.eatit.ui.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.model.Restaurant
import com.example.eatit.ui.theme.EatItTheme
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EatItCard(onItemClicked: () -> Unit, function: @Composable () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,
        onClick = onItemClicked,
        content = @Composable {
            function()
        }
    )
}

@Composable
fun RestaurantCard(
    restaurant: DocumentSnapshot,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel
) {
    EatItCard(onItemClicked = {
        val restaurantt = restaurant.toObject(Restaurant::class.java)
        restaurantt?.id = restaurant.id
        restaurantsViewModel.selectRestaurant(restaurantt!!)
        onItemClicked()
    }) {
        Row {
            Surface(modifier = Modifier.size(100.dp)) {
                EatItImageCircle(restaurant.data!!["photo"].toString())
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = restaurant.data!!["name"].toString(),
                    modifier = Modifier.padding(4.dp),
                    fontSize = 28.sp
                )
                Text(
                    text = restaurant.data!!["city"].toString(),
                    modifier = Modifier.padding(4.dp),
                    fontSize = 16.sp
                )
                var rating: Float by remember { mutableStateOf(3.2f) }
                RatingBar(
                    value = restaurant.data!!["avgRating"].toString().toFloat(),
                    style = RatingBarStyle.Fill(),
                    onValueChange = {
                        rating = it
                    },
                    onRatingChanged = {
                        Log.d("TAG", "onRatingChanged: $it")
                    },
                    modifier = Modifier.padding(4.dp),
                    size = 20.dp
                )

            }
        }

    }
}

@Composable
fun ProductCard(product: DocumentSnapshot) {
    EatItCard(onItemClicked = {
    }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = product.data!!["name"].toString(),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = product.data!!["description"].toString(),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 15.sp
                )
            }

            Text(
                text = product.data!!["price"].toString() + "€",
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp
            )
            QuantitySelector(
                count = 0,
                decreaseItemCount = { /*TODO*/ },
                increaseItemCount = { /*TODO*/ })
        }
    }
}

@Composable
fun RatingCard(rating: DocumentSnapshot) {
    EatItCard(onItemClicked = {
    }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = rating.data!!["text"].toString(),
                modifier = Modifier.padding(4.dp),
                fontSize = 20.sp
            )
            Row {
                Text(
                    text = rating.data!!["userName"].toString(),
                    modifier = Modifier.padding(4.dp),
                    fontSize = 15.sp
                )
                var valrating: Float by remember { mutableStateOf(3.2f) }
                RatingBar(
                    value = rating.data!!["rating"].toString().toFloat(),
                    style = RatingBarStyle.Fill(),
                    onValueChange = {
                        valrating = it
                    },
                    onRatingChanged = {
                        Log.d("TAG", "onRatingChanged: $it")
                    },
                    modifier = Modifier.padding(4.dp),
                    size = 20.dp
                )
            }


        }
    }
}
@Composable
fun OrderCard(
    customerName: String,
    customerAddress: String,
    total: Float,
    orderDate: String
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    EatItCard(onItemClicked = {}) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = orderDate,
                modifier = Modifier.rotate(-90f),
                textAlign = TextAlign.Center,
                lineHeight = 0.sp,
                fontSize = 20.sp
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = customerName,
                    fontSize = 20.sp
                )

                Text(
                    text = customerAddress,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.size(25.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Totale:",
                        fontWeight = Bold,
                        fontSize = 20.sp
                    )

                    Text(
                        text = "€ $total",
                        fontWeight = Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }

        if (expandedState) {
            Divider(modifier = Modifier.padding(10.dp))
            Text(
                modifier = Modifier.padding(20.dp, 0.dp),
                text = "Ordine:",
                fontWeight = Bold,
                fontSize = 25.sp,
                overflow = TextOverflow.Ellipsis
            )
            Column(
                //Non si possono utilizzare lazycolumn qui :(
                modifier = Modifier.padding(20.dp, 0.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Patate",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "x5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "€ 3.5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Patate",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "x5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "€ 3.5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Patate",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "x5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "€ 3.5",
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center
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

@Preview("default")
@Composable
fun Preview() {
    EatItTheme {
        OrderCard(
            customerName = "Mario Rossi",
            customerAddress = "Via Roma 1",
            total = 10f,
            orderDate = "12/12/2021"
        )
    }
}