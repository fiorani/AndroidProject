package com.example.eatit.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMenuScreen(modifier: Modifier = Modifier) {
    val orders = listOf<String>("A", "A", "A", "A", "A", "A")
    var cont = 0
    var cont2 = 0
    val isSurfaceOpen = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {} /*onAddButtonClicked*/) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_restaurant)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Text(
                    text = "Menù",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
            //foreach per ogni portata
            while (cont2++ < 3) {
                cont = 0
                Text(
                    text = "Antipasti:",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                while (cont++ < 6) {
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearOutSlowInEasing
                                    )
                                )
                                .padding(10.dp, 1.dp),
                            onClick = {
                                isSurfaceOpen.value = true
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                Text("Sufflet")
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("€4")
                                    IconButton(
                                        onClick = {}
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(80.dp))
            if (isSurfaceOpen.value) {
                AlertDialog(
                    onDismissRequest = {
                        isSurfaceOpen.value = false
                        cont = 0
                        cont2 = 0
                    },
                ) {
                    DishEdit(
                        isSurfaceOpen,
                        onConfirm = {
                            isSurfaceOpen.value =
                                false // Aggiorna isSurfaceOpen quando viene premuto "Confirm"
                            cont = 0
                            cont2 = 0
                        }
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishEdit(
    isSurfaceOpen: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    val dishTypes = listOf("Antipasti", "Primi", "Secondi", "Dolci", "Bevande")

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Modifica pietanza:",
            )

            Box {
                dishTypes.forEach { type ->
                    AssistChip(
                        onClick = { /* Do something! */ },
                        label = { Text(type) }
                    )
                }
            }

            var txtProduct by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(""))
            }
            OutlinedTextField(
                value = txtProduct,
                onValueChange = { txtProduct = it },
                label = { Text("Nome pietanza") }
            )

            var txtPrice by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue(""))
            }
            OutlinedTextField(
                value = txtPrice,
                onValueChange = {
                    if (it.text.toDoubleOrNull() != null) {
                        txtPrice = it
                    } //val tmp = it.text.substring(0, it.text.indexOf(",") + 2) 
                },
                label = { Text("Prezzo") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = {
                    isSurfaceOpen.value = false
                    onConfirm() // Chiama la funzione onConfirm quando il pulsante viene premuto
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Confirm")
            }
        }
    }
}