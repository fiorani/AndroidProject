package com.example.eatit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.model.Restaurant

@Composable
fun EatItButton(modifier: Modifier = Modifier, enabled: Boolean = true, text:String, function: () -> Unit, icon: ImageVector? = null, txtsize: Int = 20, pad: Int = 5){
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = {
        function()
    }) {
        Text(
            modifier = modifier.padding(pad.dp),
            text = text,
            fontSize = txtsize.sp,
            textAlign = TextAlign.Center
        )
        if (icon != null) {
            Icon(
                icon,
                contentDescription = text
            )
        }
    }
}

@Composable
fun EatItFloatingButton(function: () -> Unit, icon: ImageVector ){
    FloatingActionButton(
        shape = RoundedCornerShape(20.dp),
        onClick = {
            function()
        }
    ) {
        Icon(
            icon,
            contentDescription = icon.toString()
        )
    }
}
@Composable
fun EatItIconButton(function: (() -> Unit)? =null, icon: ImageVector ){
    IconButton(onClick = {
        if (function != null) {
            function()
        }
    }) {
        Icon(
            icon,
            contentDescription = icon.toString()
        )
    }
}

@Preview
@Composable
fun EatItButtonPreview(){
    Column {
        EatItButton(
            text = "Add",
            function = {},
            icon = Icons.Default.Add
        )
        EatItFloatingButton(
            function = {},
            icon = Icons.Default.Add
        )
        EatItIconButton(
            function = {},
            icon = Icons.Default.Add
        )
    }
}

@Composable
fun ChangeImageButton(photo: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ){
        ImageCard(
            photo,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
        )
        Icon(Icons.Default.PhotoCamera, "Photo", tint = MaterialTheme.colorScheme.background)
    }
}