package com.example.eatit.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EatItButton(modifier: Modifier = Modifier, enabled: Boolean = true, text:String, function: () -> Unit, icon: ImageVector? = null ){
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = {
        function()
    }) {
        Text(
            text = text,
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