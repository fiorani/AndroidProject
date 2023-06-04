package com.example.eatit.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
) {
    Scaffold()
    { paddingValues ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                SectionCard("Primi", listOf("Pollo", "Soya", "Mangime"))
                SectionCard("Secondi", listOf("Pollo", "Soya", "Mangime"))
                SectionCard("Terzi", listOf("Pollo", "Soya", "Mangime"))
                SectionCard("Quarti", listOf("Pollo", "Soya", "Mangime"))
            }

            Button(
                modifier = Modifier.padding(10.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    modifier = Modifier.padding(20.dp, 10.dp),
                    text = "Il mio ordine",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionCard(
    sectionName: String,
    products: List<String>
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp, 10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = CardDefaults.shape,
        onClick = {
            expandedState = !expandedState
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(20.dp, 10.dp),
                text = sectionName,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                modifier = Modifier
                    .rotate(rotationState),
                onClick = {
                    expandedState = !expandedState
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Drop-Down Arrow"
                )
            }
        }
    }

    if (expandedState) {
        for (product in products) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = "Agriculture"
                    )
                    Text(
                        modifier = Modifier.padding(10.dp, 0.dp),
                        text = product,
                        fontSize = 20.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove"
                        )
                    }
                    Text(
                        modifier = Modifier.padding(10.dp, 0.dp),
                        text = "0",
                        fontSize = 20.sp
                    )
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            }
        }
    }

}