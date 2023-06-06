package com.example.eatit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eatit.ui.components.OrderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantHomeScreen() {
    val orders = listOf<String>("A", "A", "A", "A", "A", "A")
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    Scaffold {
        Column {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { active = false },
                active = active,
                onActiveChange = { active = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                /*val filteredData = orders.filter { item ->
                    //item.orderId.contains(query, ignoreCase = true)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(filteredData) { customerName ->
                        OrderCard(customerName = "Marco", customerAddress= "Via O. Brobrio", total = 14.60f, orderDate = "19-10-2023")
                    }
                }
            }

            LazyColumn {
                items(items = orders) { order ->
                    OrderCard(
                        customerName = "Marco",
                        customerAddress = "Via O. Brobrio",
                        total = 14.60f,
                        orderDate = "19-10-2023"
                    )
                }*/
            }
        }
    }
}