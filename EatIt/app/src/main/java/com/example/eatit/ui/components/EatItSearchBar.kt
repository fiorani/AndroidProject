package com.example.eatit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EatItSearchBar(
    restaurants: Task<QuerySnapshot>,
    onItemClicked: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel
) {
    var active by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    SearchBar(
        query = query,
        onQueryChange = {
            query = it
        },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        var searchResults = remember { mutableStateListOf<DocumentSnapshot>() }
        searchResults.clear()
        restaurants.addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                if (document.data.get("name").toString()
                        .contains(query, ignoreCase = true)
                ) {
                    searchResults.add(document)
                }
            }
        }.addOnFailureListener { exception ->
            println("Error getting restaurants: $exception")
        }
        LazyColumn {
            items(searchResults.size) { index ->
                RestaurantCard(
                    searchResults[index],
                    onItemClicked,
                    restaurantsViewModel
                )
            }
        }
    }
}