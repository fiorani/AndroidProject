package com.example.eatit.ui

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
import com.example.eatit.ui.components.BackgroundImage
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
    restaurantsViewModel: RestaurantsViewModel,

    usersViewModel: UsersViewModel
) {
    var colorCustomer = MaterialTheme.colorScheme.tertiary
    var colorRestaurant = MaterialTheme.colorScheme.primary

    Scaffold { innerPadding ->
        BackgroundImage(alpha = 0.15f)
        Column(
            modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val isUserRegister = remember { mutableStateOf(true) }
            var strTitle = "User registration"
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(65.dp, 20.dp, 65.dp, 0.dp),
                text = "Do you want to register as a customer or as a restaurant?",
                fontSize = 20.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (isUserRegister.value){
                    colorCustomer = MaterialTheme.colorScheme.tertiary
                    colorRestaurant = MaterialTheme.colorScheme.primary
                } else {
                    colorRestaurant = MaterialTheme.colorScheme.tertiary
                    colorCustomer = MaterialTheme.colorScheme.primary
                }
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        isUserRegister.value = true
                        strTitle = "User registration"
                    },
                    colors = ButtonDefaults.buttonColors(colorCustomer),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Text(
                        text = "Customer",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        isUserRegister.value = false
                        strTitle = "Restaurant registration"
                    },
                    colors = ButtonDefaults.buttonColors(colorRestaurant),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                ) {
                    Text(
                        text = "Restaurant",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp, 20.dp, 40.dp, 0.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = strTitle,
                        fontSize = 25.sp
                    )

                    if (isUserRegister.value) {
                        var txtNickname by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(""))
                        }
                        OutlinedTextField(
                            value = txtNickname,
                            onValueChange = { txtNickname = it },
                            label = { Text("Nickname") }
                        )
                    }

                    if (!isUserRegister.value) {
                        var txtDenomination by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue(""))
                        }
                        OutlinedTextField(
                            value = txtDenomination,
                            onValueChange = { txtDenomination = it },
                            label = { Text("Denomination") }
                        )
                    }

                    var txtName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtName,
                        onValueChange = { txtName = it },
                        label = { Text("Name") }
                    )

                    var txtSurname by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtSurname,
                        onValueChange = { txtSurname = it },
                        label = { Text("Surname") }
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
                    }

                    if (isUserRegister.value) {
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

                    var city by rememberSaveable { mutableStateOf("") }
                    LaunchedEffect(Unit) {
                        city = usersViewModel.getPosition()
                    }
                    OutlinedTextField(
                        value = city,
                        onValueChange = { newText ->
                            city = newText
                        },
                        label = {
                            Text(stringResource(id = R.string.label_city))
                        },
                        modifier = Modifier.weight(4f)
                    )

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = {
                                createAccount(
                                    txtEmail.text,
                                    txtPassword.text,
                                    txtName.text,
                                    "",
                                    0,
                                    city,
                                    !isUserRegister.value,
                                    onNextButtonClicked
                                )
                            },
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                        ) {
                            Text(
                                text = "Register",
                                textAlign = TextAlign.Center,
                                fontSize = 25.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(70.dp, 40.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Column {
                    Text(
                        modifier = Modifier
                            .padding(10.dp, 0.dp),
                        text = "Already have an account?",
                        fontSize = 20.sp
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp, 5.dp),
                        onClick = {
                            onLoginButtonClicked()
                        },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                    ) {
                        Text(
                            text = "Login",
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp
                        )
                    }
                }
            }
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