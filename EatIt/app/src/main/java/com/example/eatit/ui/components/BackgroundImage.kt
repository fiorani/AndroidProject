package com.example.eatit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun BackgroundImage(alpha :Float){
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val imageModifier = Modifier.fillMaxSize()
        val backgroundImage = painterResource(id = LocalContext.current.resources.getIdentifier("background_image", "drawable", LocalContext.current.packageName))

        Image(
            painter = backgroundImage,
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.FillBounds,
            alpha = alpha
        )
    }
}