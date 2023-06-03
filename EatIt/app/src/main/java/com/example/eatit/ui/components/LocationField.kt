package com.example.eatit.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eatit.R

@Composable
fun LocationField(
    startLocationUpdates: () -> Unit
) {
    Row {
        var location by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            value = location,
            onValueChange = { newText ->
                location = newText
            },
            label = {
                Text(stringResource(R.string.txtLocation))
            },
            modifier = Modifier.weight(4f)
        )

        Icon(
            Icons.Filled.LocationSearching,
            contentDescription = "get gps",
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .clickable(onClick = startLocationUpdates)
        )
    }
}