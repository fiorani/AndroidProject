package com.example.eatit.ui.components


import android.content.res.Configuration
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eatit.R

@Composable
fun QuantitySelector(
    count: Int,
    decreaseItemCount: () -> Unit,
    increaseItemCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = "Quantity",
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = decreaseItemCount) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Drop-Down Arrow"
            )
        }
        Crossfade(
            targetState = count,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(
                text = "$it"
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = increaseItemCount) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Drop-Down Arrow"
            )
        }
    }
}

@Preview("default")
@Composable
fun QuantitySelectorPreview() {
    QuantitySelector(1, {}, {})
}