package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(modifier: Modifier = Modifier) {
    val orders = listOf<String>("A","A","A","A","A","A")
    var cont = 0
    var cont2 = 0
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {} /*onAddButtonClicked*/ ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_restaurant))
            }
        }
    ) { innerPadding ->
        Column (
            modifier.padding(innerPadding).verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth().padding(10.dp),
            ) {
                Text(
                    text = "Menù",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }
            //foreach per ogni portata
            while(cont2++ < 3) {
                cont = 0
                Text(
                    text = "Antipasti:",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                while(cont++ < 6) {
                    Column(){
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearOutSlowInEasing
                                    )
                                )
                                .padding(10.dp, 1.dp),
                            onClick = { /* do something */ }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(10.dp)
                            ) {
                                Text("Sufflet")
                                Text("€4")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(80.dp))
        }
    }
}