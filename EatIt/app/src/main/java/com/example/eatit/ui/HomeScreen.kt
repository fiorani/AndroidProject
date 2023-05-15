package com.example.eatit.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.viewModel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddButtonClicked: () -> Unit,
    onItemClicked:  () -> Unit,
    placesViewModel: PlacesViewModel,
    modifier: Modifier = Modifier) {

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick =  onAddButtonClicked ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_travel))
            }
        },
    ) { innerPadding ->
        Column (modifier.padding(innerPadding)) {
            TravelsList(onItemClicked, placesViewModel)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelsList(onItemClicked: () -> Unit, placesViewModel: PlacesViewModel) {
    val places = placesViewModel.places.collectAsState(initial = listOf()).value
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        content = {
            items(items= places) { place ->
                Card(
                    onClick =  {
                        placesViewModel.selectPlace(place)
                        onItemClicked()
                    },
                    modifier = Modifier
                        .size(width = 150.dp, height = 150.dp)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =  MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(all = 12.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (place.travelPhoto.isEmpty()) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_android_24),
                                contentDescription = "travel image",
                                modifier = Modifier
                                    .clip(shape = CircleShape)
                                    .size(size = 50.dp),
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer)
                            )
                        } else {
                            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                                .data(Uri.parse(place.travelPhoto))
                                .crossfade(true)
                                .build(),
                                contentDescription = "image of the place",
                                modifier = Modifier
                                    .clip(shape = CircleShape)
                                    .size(size = 50.dp))
                        }

                        val scroll = rememberScrollState(0)
                        Text(
                            text = place.placeName,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.verticalScroll(scroll)
                        )
                    }
                }
            }
        }
    )
}


