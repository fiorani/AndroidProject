package com.example.eatit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(modifier: Modifier = Modifier) {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row() {
                            Icon(Icons.Filled.LunchDining, contentDescription = stringResource(id = R.string.back))
                            Text(stringResource(id = R.string.app_name))
                        }},
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Filled.Settings, contentDescription = stringResource(id = R.string.back))
                        }
                    }

                )
            },
        ) { innerPadding ->
            Column (modifier.padding(innerPadding)) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                ) {
                    Row() {
                        Icon(Icons.Filled.AccountCircle, contentDescription = stringResource(id = R.string.back), modifier = Modifier.size(100.dp))
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

            }
        }
}