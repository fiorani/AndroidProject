package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantProfileScreen(modifier: Modifier = Modifier) {
    Scaffold () { innerPadding ->
        Column (modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Row() {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.back),
                        modifier = Modifier.size(100.dp)
                    )
                    Column {
                        Text(
                            text = "Company Name",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 32.sp
                        )
                        Text(
                            text = "Company Address",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(60.dp, 5.dp),
                onClick = { /* Do something! */ },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
            ) {
                Text(
                    text = "Menù",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
            }
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
                onClick = { /* do something */ }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Histogram",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 32.sp
                        )
                        Text(
                            text = "Fare disegno istogramma",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
    }
}