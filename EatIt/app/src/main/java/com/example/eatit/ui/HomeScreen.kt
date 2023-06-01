package com.example.eatit.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.model.Restaurant
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await

@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked:  () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier) {
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick =  onAddButtonClicked ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_restaurant))
            }
        },
    ) { innerPadding ->
        Column (modifier.padding(innerPadding)) {
            ristorantList(onItemClicked, restaurantsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ristorantList(onItemClicked: () -> Unit, restaurantsViewModel: RestaurantsViewModel) {
    val db = FirebaseFirestore.getInstance()
    val restaurantsCollection = db.collection("restaurants")
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val restaurants = remember { mutableStateListOf<DocumentSnapshot>() }
    restaurantsCollection.get()
        .addOnSuccessListener { querySnapshot ->
            restaurants.addAll(querySnapshot.documents)
        }
        .addOnFailureListener { exception ->
            println("Error getting restaurants: $exception")
        }
    Column() {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            modifier = Modifier.fillMaxWidth()
        ) {

            LazyColumn() {
                items(restaurants.size) { index ->
                    val restaurant = restaurants[index]
                    ristorantCard(restaurant,onItemClicked, restaurantsViewModel)
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(restaurants.size) { index ->
            val restaurant = restaurants[index]
            ristorantCard(restaurant,onItemClicked, restaurantsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ristorantCard(
    restaurant: DocumentSnapshot,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel) {
    Card(
        onClick = {
            onItemClicked()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,

        ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = restaurant.data!!["name"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            Text(
                text = restaurant.data!!["address"].toString(),
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
        }
    }
}



