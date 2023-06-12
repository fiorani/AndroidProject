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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.R
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
    createAccount: KFunction9<String, String, String, String, String, Boolean, String, String, () -> Unit, Unit>,
    onNextButtonClicked: () -> Unit,
    onLoginButtonClicked: () -> Unit,
    startLocationUpdates: () -> Unit,
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
            var strTitle = stringResource(R.string.user_reg)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp, 20.dp, 40.dp, 5.dp),
                text = stringResource(R.string.customer_or_restaurant),
                fontSize = 20.sp,
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                EatItButton(modifier = Modifier.padding(3.dp), text = stringResource(R.string.customer), function = {
                    isUserRegister.value = true
                    strTitle = "User"
                })
                EatItButton(modifier = Modifier.padding(3.dp), text = stringResource(R.string.restaurant), function = {
                    isUserRegister.value = false
                    strTitle = "Restaurant"
                })
                if (strTitle == "User") strTitle = stringResource(R.string.user_registration)
                if (strTitle == "Restaurant") strTitle = stringResource(R.string.restaurant_registration)
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
                        label = { Text(stringResource(R.string.nickname)) }
                    )

                    var password by rememberSaveable { mutableStateOf("") }
                    var passwordHidden by rememberSaveable { mutableStateOf(true) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password2)) },
                        visualTransformation =
                        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                val visibilityIcon =
                                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description =
                                    if (passwordHidden) stringResource(R.string.show_pw2) else stringResource(
                                                                            R.string.hide_pw2)
                                Icon(imageVector = visibilityIcon, contentDescription = description)
                            }
                        }
                    )

                    var birth by rememberSaveable { mutableStateOf("") }
                    var piva by rememberSaveable { mutableStateOf("") }

                    if (!isUserRegister.value) {
                        OutlinedTextField(
                            value = piva,
                            onValueChange = { piva = it },
                            label = { Text(stringResource(R.string.p_iva)) }
                        )
                    } else {
                        // date picker not fully working: 'ok' button not broken anymore, not checking for future dates.
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
                            label = { Text(stringResource(R.string.birthday)) }
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
                                            birth = TextFieldValue(getDate(datePickerState.selectedDateMillis)).text
                                        },
                                        enabled = confirmEnabled.value
                                    ) {
                                        Text(stringResource(R.string.ok))
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            openDialog.value = false
                                        }
                                    ) {
                                        Text(stringResource(R.string.cancel2))
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
                        label = { Text(stringResource(R.string.address)) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = stringResource(R.string.localized),
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
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = VisualTransformation.None,
                        label = { Text(stringResource(R.string.phone_number)) },
                    )

                    var email by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email2)) }
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    EatItButton(text = stringResource(R.string.register2), function = {
                        createAccount(
                            email,
                            password,
                            name,
                            "",
                            birth,
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
                text = stringResource(R.string.already_account),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.padding(5.dp))
            EatItButton(text = stringResource(R.string.login3), function = { onLoginButtonClicked() })
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

fun getDate(timestamp: Long?): String {
    if (timestamp != null) {
        val calendar = Calendar.getInstance(Locale.ITALIAN)
        calendar.timeInMillis = timestamp
        return DateFormat.format("dd-MM-yyyy", calendar).toString()
    }
    return ""
}