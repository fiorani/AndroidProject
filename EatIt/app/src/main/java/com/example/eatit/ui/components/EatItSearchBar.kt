package com.example.eatit.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.unit.dp
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.firebase.firestore.DocumentSnapshot


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EatItSearchBar(
    restaurants: List<DocumentSnapshot>,
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
        placeholder = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    fontStyle = Italic,
                    text = "Search a restaurant..."
                )
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 2.dp)
    ) {
        var searchResults = remember { mutableStateListOf<DocumentSnapshot>() }
        searchResults.clear()
        restaurants.forEach { document ->
            if (document.getString("name")?.contains(query, ignoreCase = true) == true) {
                searchResults.add(document)
            }
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