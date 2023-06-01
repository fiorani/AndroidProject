package com.example.eatit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.viewModel.RestaurantsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRestaurantScreen(restaurantsViewModel: RestaurantsViewModel, onAddButtonClicked: () -> Unit) {
    /*val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick =  onAddButtonClicked ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_restaurant))
            }
        },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            if (selectedRestaurant?.restaurantPhoto?.isEmpty() == true) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_android_24),
                    contentDescription = "image restaurantholder",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(selectedRestaurant?.restaurantPhoto))
                        .crossfade(true)
                        .build(),
                    contentDescription = "image of the restaurant",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
        }

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = selectedRestaurant?.restaurantName?:stringResource(id = R.string.restaurant_name),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = selectedRestaurant?.restaurantDescription?:stringResource(id = R.string.restaurant_description),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }*/
}
/*
private fun shareDetails(context: Context, restaurant: Restaurant?){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, restaurant?.restaurantName?:"No restaurant")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}*/