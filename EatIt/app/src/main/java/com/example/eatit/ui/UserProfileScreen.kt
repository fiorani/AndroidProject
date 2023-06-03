package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(modifier: Modifier = Modifier) {
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Row {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.back),
                        modifier = Modifier.size(100.dp)
                    )
                    Column {
                        Text(
                            text = "User Name",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 32.sp
                        )
                        Text(
                            text = "User Address",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
            Text(
                text = "Order list:",
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            val data = remember {
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(data) { item ->
                    orderCard(item)
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun orderCard(item: Any) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .padding(8.dp),
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "data, address",
                modifier = Modifier.padding(8.dp),
                fontSize = 32.sp
            )
            Text(
                text = "totale = ",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp
            )
            if (expandedState) {
                Text(
                    text = "description",
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
        }
    }
}


