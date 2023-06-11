package com.example.eatit.ui.components


import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = decreaseItemCount
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Remove item"
            )
        }
        Text(
            text = count.toString(),
            fontSize = 20.sp
        )
        IconButton(
            onClick = increaseItemCount
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add item"
            )
        }
    }
}

@Preview("default")
@Composable
fun QuantitySelectorPreview() {
    QuantitySelector(1, {}, {})
}