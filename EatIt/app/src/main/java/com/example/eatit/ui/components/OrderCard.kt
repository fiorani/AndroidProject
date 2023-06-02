package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    customerName: String,
    customerAddress: String,
    total: Float,
    orderDate: String
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
            .padding(10.dp),
    ) {
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