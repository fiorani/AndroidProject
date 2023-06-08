package com.example.eatit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.EatItButton
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
        Column(
            modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(
                    modifier= Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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
                    Spacer(modifier = Modifier.padding(10.dp))
                    EatItButton(
                        text = "Login",
                        function = { signIn(txtName.text, txtPassword.text, onNextButtonClicked) },
                        icon = Icons.Default.Login
                    )
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Don't have an account yet?",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.padding(10.dp))
            EatItButton(
                text = "Register",
                function = { onRegisterClicked() },
                icon = Icons.Default.AppRegistration
            )
        }
    }
}