package com.example.eatit.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R

@Composable
fun ImageCard(photo: String, modifier: Modifier = Modifier
    .fillMaxWidth()
    .height(150.dp)) {
    if (photo != "") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo))
                .crossfade(true)
                .build(),
            contentDescription = "image of the restaurant",
            modifier = modifier,
            contentScale = ContentScale.FillWidth,
        )

    } else {
        Image(
            painter = painterResource(id = R.drawable.baseline_no_photography_24),
            contentDescription = "travel image",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer)
        )
    }
}

@Composable
fun ImageProfile(photo: String) {
    if (photo != "") {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(Uri.parse(photo))
                .crossfade(true)
                .build(),
            contentDescription = "image of the restaurant",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                },
            contentScale = ContentScale.FillWidth,
        )

    } else {
        Image(
            painter = painterResource(id = R.drawable.baseline_no_photography_24),
            contentDescription = "travel image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                },
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondaryContainer)
        )
    }
}

@Composable
fun BackgroundImage(alpha: Float) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val imageModifier = Modifier.fillMaxSize()
        val backgroundImage = painterResource(
            id = LocalContext.current.resources.getIdentifier(
                "background_image",
                "drawable",
                LocalContext.current.packageName
            )
        )

        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.FillBounds,
            alpha = alpha
        )
    }
}