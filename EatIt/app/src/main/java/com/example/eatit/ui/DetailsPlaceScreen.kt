package com.example.eatit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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
import com.example.eatit.data.Place
import com.example.eatit.viewModel.PlacesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(placesViewModel: PlacesViewModel) {
    val context = LocalContext.current
    val selectedPlace = placesViewModel.placeSelected
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { shareDetails(context, selectedPlace) }) {
                Icon(Icons.Filled.Share, contentDescription = stringResource(id = R.string.add_restaurant))
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            if (selectedPlace?.travelPhoto?.isEmpty() == true) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_android_24),
                    contentDescription = "image placeholder",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer)
                )
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(selectedPlace?.travelPhoto))
                        .crossfade(true)
                        .build(),
                    contentDescription = "image of the place",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = selectedPlace?.placeName?:stringResource(id = R.string.place_name),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(15.dp))

            Text(
                text = selectedPlace?.placeDescription?:stringResource(id = R.string.place_description),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun shareDetails(context: Context, place: Place?){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, place?.placeName?:"No place")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}