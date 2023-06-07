package com.example.eatit.ui

import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.ui.components.BackgroundImage
import kotlin.reflect.KFunction3


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    signIn: KFunction3<String, String, () -> Unit, Unit>,
    onRegisterClicked: () -> Unit,
    onNextButtonClicked: () -> Unit
) {
    Scaffold { innerPadding ->
        BackgroundImage(0.15f)
        Column(modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp, 130.dp, 40.dp, 40.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Login",
                        fontSize = 32.sp,
                        fontWeight = Bold
                    )

                    var txtName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }

                    OutlinedTextField(
                        value = txtName,
                        onValueChange = { txtName = it },
                        label = { Text("Name") }
                    )

                    var txtPassword by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                        mutableStateOf(TextFieldValue(""))
                    }

                    OutlinedTextField(
                        value = txtPassword,
                        onValueChange = { txtPassword = it },
                        label = { Text("Password") }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            modifier = Modifier.padding(10.dp),
                            onClick = {
                                signIn(txtName.text, txtPassword.text, onNextButtonClicked)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(70.dp, 0.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Don't have an account yet?",
                        fontSize = 20.sp
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(55.dp, 5.dp),
                        onClick = {
                            onRegisterClicked()
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
    }
}