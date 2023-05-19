package com.example.eatit.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
import com.example.eatit.viewModel.PlacesViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegisterScreen(modifier: Modifier = Modifier, placesViewModel: PlacesViewModel, startLocationUpdates: () -> Unit) {
    Scaffold () { innerPadding ->
        Column (modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp,50.dp,40.dp, 40.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register",
                        fontSize = 32.sp
                    )

                    var txtNickname by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }
                    OutlinedTextField(
                        value = txtNickname,
                        onValueChange = { txtNickname = it },
                        label = { Text("Nickname") }
                    )

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

                    // date picker not fully working: 'ok' button broken, not checking for future dates.
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
                            if(it.isFocused){
                                openDialog.value = true
                            }
                        },
                        label = { Text("Birthday") }
                    )

                    if (openDialog.value) {
                        val datePickerState = rememberDatePickerState()
                        val confirmEnabled = remember {derivedStateOf { datePickerState.selectedDateMillis != null }}
                        DatePickerDialog(
                            onDismissRequest = {
                                openDialog.value = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        openDialog.value = false
                                        txtBirth = TextFieldValue(getDate(datePickerState.selectedDateMillis))
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

                    //Maps: Don't know if it's working correctly
                    Row() {
                        var title by rememberSaveable { placesViewModel.placeFromGPS }
                        OutlinedTextField(
                            value = title,
                            onValueChange = { newText ->
                                title = newText
                            },
                            label = {
                                Text(stringResource(id = R.string.place_title))
                            },
                            modifier = Modifier.weight(4f)
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.baseline_gps_fixed_24),
                            contentDescription = "get gps",
                            modifier = Modifier.weight(1f).padding(20.dp).clickable(onClick = startLocationUpdates)
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = { /* Do something! */ },
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
                    .padding(70.dp, 0.dp),
                horizontalArrangement= Arrangement.Center,
            ){
                Column() {
                    Text(
                        text = "Already have an account?",
                        fontSize = 20.sp
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp, 5.dp),
                        onClick = { /* Do something! */ },
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

fun getDate(timestamp: Long?) :String {
    if (timestamp != null){
        val calendar = Calendar.getInstance(Locale.ITALIAN)
        calendar.timeInMillis = timestamp
        val date = DateFormat.format("dd-MM-yyyy",calendar).toString()
        return date
    }
    return ""
}