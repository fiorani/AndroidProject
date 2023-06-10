package com.example.eatit.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.EatItButton
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import java.util.*
import kotlin.reflect.KFunction9

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    createAccount: KFunction9<String, String, String, String, Int, Boolean, String, String, () -> Unit, Unit>,
    onNextButtonClicked: () -> Unit,
    onLoginButtonClicked: () -> Unit,
    startLocationUpdates: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,

    usersViewModel: UsersViewModel
) {
    Scaffold { innerPadding ->
        BackgroundImage(alpha = 0.15f)
        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isUserRegister = remember { mutableStateOf(true) }
            var strTitle = "User registration"
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp, 20.dp, 40.dp, 5.dp),
                text = "Do you want to register as a customer or as a restaurant?",
                fontSize = 20.sp,
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                EatItButton(modifier = Modifier.padding(3.dp), text = "Customer", function = {
                    isUserRegister.value = true
                    strTitle = "User registration"
                })
                EatItButton(modifier = Modifier.padding(3.dp), text = "Restaurant", function = {
                    isUserRegister.value = false
                    strTitle = "Restaurant registration"
                })
            }
            Spacer(modifier = Modifier.size(20.dp))
            Card(
                modifier = Modifier.padding(40.dp, 10.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = strTitle,
                        fontSize = 25.sp,
                        fontWeight = Bold
                    )

                    var name by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nickname") }
                    )

                    var password by rememberSaveable { mutableStateOf("") }
                    var passwordHidden by rememberSaveable { mutableStateOf(true) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation =
                        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                val visibilityIcon =
                                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description =
                                    if (passwordHidden) "Show password" else "Hide password"
                                Icon(imageVector = visibilityIcon, contentDescription = description)
                            }
                        }
                    )

                    if (!isUserRegister.value) {
                        var piva by rememberSaveable { mutableStateOf("") }
                        OutlinedTextField(
                            value = piva,
                            onValueChange = { piva = it },
                            label = { Text("P.IVA") }
                        )
                    } else {
                        // date picker not fully working: 'ok' button not broken anymore, not checking for future dates.
                        var birth by rememberSaveable { mutableStateOf("") }
                        val openDialog = remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = birth,
                            onValueChange = {//from the second time on you select the text field
                                openDialog.value = true
                            },
                            modifier = Modifier.onFocusEvent {//for the first time you select the text field
                                if (it.isFocused) {
                                    openDialog.value = true
                                }
                            },
                            label = { Text("Birthday") }
                        )

                        if (openDialog.value) {
                            val datePickerState = rememberDatePickerState()
                            val confirmEnabled =
                                remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
                            DatePickerDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            openDialog.value = false
                                            birth =
                                                TextFieldValue(getDate(datePickerState.selectedDateMillis)).toString()
                                        },
                                        enabled = confirmEnabled.value
                                    ) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                    }

                    var address by rememberSaveable { usersViewModel.position }
                    OutlinedTextField(
                        value = address,
                        onValueChange = { newText -> address = newText },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Localized",
                                Modifier
                                    .clickable(onClick = {
                                        startLocationUpdates()
                                    })
                            )
                        }
                    )

                    var phone by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone number") }
                    )

                    var email by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    EatItButton(text = "Register", function = {
                        createAccount(
                            email,
                            password,
                            name,
                            "",
                            0,
                            !isUserRegister.value,
                            address,
                            phone,
                            onNextButtonClicked
                        )
                    })

                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                modifier = Modifier
                    .padding(5.dp, 0.dp),
                text = "Already have an account?",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.padding(5.dp))
            EatItButton(text = "Login", function = { onLoginButtonClicked() })
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

fun getDate(timestamp: Long?): String {
    if (timestamp != null) {
        val calendar = Calendar.getInstance(Locale.ITALIAN)
        calendar.timeInMillis = timestamp
        val date = DateFormat.format("dd-MM-yyyy", calendar).toString()
        return date
    }
    return ""
}