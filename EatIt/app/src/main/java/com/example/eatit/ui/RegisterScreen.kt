package com.example.eatit.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.EatItButton
import com.example.eatit.viewModel.RestaurantsViewModel
import com.example.eatit.viewModel.UsersViewModel
import java.util.*
import kotlin.reflect.KFunction8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    createAccount: KFunction8<String, String, String, String, Int, String, Boolean, () -> Unit, Unit>,
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
                    .padding(40.dp, 20.dp),
                text = "Do you want to register as a customer or as a restaurant?",
                fontSize = 20.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                EatItButton(text = "Customer", function = {
                    isUserRegister.value = true
                    strTitle = "User registration"
                })
                EatItButton(text = "Restaurant", function = {
                    isUserRegister.value = false
                    strTitle = "Restaurant registration"
                })
            }
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
                        fontSize = 25.sp
                    )

                    var txtNickname by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtNickname,
                        onValueChange = { txtNickname = it },
                        label = { Text("Nickname") }
                    )

                    var txtPassword by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtPassword,
                        onValueChange = { txtPassword = it },
                        label = { Text("Password") }
                    )

                    if (!isUserRegister.value) {
                        var txtPIVA by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(""))
                        }
                        OutlinedTextField(
                            value = txtPIVA,
                            onValueChange = { txtPIVA = it },
                            label = { Text("P.IVA") }
                        )
                    } else {
                        // date picker not fully working: 'ok' button not broken anymore, not checking for future dates.
                        var txtBirth by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(""))
                        }
                        val openDialog = remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = txtBirth,
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
                                            txtBirth =
                                                TextFieldValue(getDate(datePickerState.selectedDateMillis))
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

                    var address by rememberSaveable { usersViewModel.userPosition }
                    LaunchedEffect(Unit) {
                        address = usersViewModel.getPosition()
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(4f),
                            value = address.ifEmpty { "" },
                            onValueChange = { newText -> address = newText },
                            label = { Text("Address") },
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


                    var txtPhone by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtPhone,
                        onValueChange = { txtPhone = it },
                        label = { Text("Phone number") }
                    )

                    var txtEmail by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtEmail,
                        onValueChange = { txtEmail = it },
                        label = { Text("Email") }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    EatItButton(text = "Register", function = {
                        createAccount(
                            txtEmail.text,
                            txtPassword.text,
                            txtNickname.text,
                            "",
                            0,
                            address,
                            !isUserRegister.value,
                            onNextButtonClicked
                        )
                    })

                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                modifier = Modifier
                    .padding(5.dp, 0.dp),
                text = "Already have an account?",
                fontSize = 20.sp
            )
            EatItButton(text = "Login", function = { onLoginButtonClicked() })
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