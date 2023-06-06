package com.example.eatit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.viewModel.SettingsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNextButtonClicked: () -> Unit,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit
) {
    Scaffold { paddingValues ->
        BackgroundImage(alpha = 0.1f)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            val user = remember { usersViewModel.getUser() }
            var name by rememberSaveable { mutableStateOf("") }
            var city by rememberSaveable { usersViewModel.userPosition }
            if (user.isNotEmpty()) {
                OutlinedTextField(
                    value = user[0].data?.get("userName").toString(),
                    onValueChange = {
                        name = it
                    },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(15.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                                newText ->
                            city = newText
                        },
                        label = { Text("Username") },
                        modifier = Modifier.weight(4f)
                    )
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Localized",
                        Modifier
                            .weight(1f)
                            .clickable(onClick = {
                                startLocationUpdates()
                            })
                    )
                }

                Spacer(modifier = Modifier.size(15.dp))
            }

            Button(
                onClick = {
                    Firebase.auth.signOut()
                    onNextButtonClicked()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}