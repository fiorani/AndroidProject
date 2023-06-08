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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eatit.EatItApp

@Composable
fun EatItButton(text:String,function: () -> Unit,icon: ImageVector? = null ){
    Button(onClick = {
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
fun EatitFloatingButton(function: () -> Unit,icon: ImageVector ){
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
fun EatItIconButton(function: () -> Unit,icon: ImageVector ){
    IconButton(onClick = {
        function()
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
    Column() {
        EatItButton(
            text = "Add",
            function = {},
            icon = Icons.Default.Add
        )
        EatitFloatingButton(
            function = {},
            icon = Icons.Default.Add
        )
        EatItIconButton(
            function = {},
            icon = Icons.Default.Add
        )
    }


}