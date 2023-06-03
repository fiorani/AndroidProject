package com.example.eatit.ui.components

import android.widget.Spinner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun DropDownSelect(){
    val dishTypes = listOf("Antipasti", "Primi", "Secondi", "Dolci", "Bevande")
    val isDropped = remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf("Antipasti") }

    Button(onClick = { isDropped.value = true}) {
        Text(text = selected.value)
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "ArrowDropDown"
        )
    }
    if(isDropped.value) {
        Surface(modifier = Modifier.zIndex(3f)) {
            Column() {
                dishTypes.forEach { type ->
                    TextButton(
                        onClick = {
                            selected.value = type
                            isDropped.value = false
                        }
                    ){
                        Text(text = type)
                    }
                }
            }
        }
    }

    /*DropdownMenu(expanded = isDropped.value, onDismissRequest = { isDropped.value = false }) {
        Surface() {
            Column() {
                dishTypes.forEach { type ->
                    TextButton(
                        onClick = {
                            selected.value = type
                            isDropped.value = false
                        }
                    ){
                        Text(text = type)
                    }
                }
            }
        }
    }*/
}