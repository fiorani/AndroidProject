package com.example.eatit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
            modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Login",
                        fontSize = 32.sp,
                        fontWeight = Bold
                    )

                    var mail by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(
                        value = mail,
                        onValueChange = { mail = it },
                        label = { Text("Email") }
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
                    Spacer(modifier = Modifier.padding(10.dp))
                    EatItButton(
                        text = "Login",
                        function = { signIn(mail, password, onNextButtonClicked) },
                        icon = Icons.Default.Login
                    )
                }
            }
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = "Don't have an account yet?",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.padding(5.dp))
            EatItButton(
                text = "Register",
                function = { onRegisterClicked() },
                icon = Icons.Default.AppRegistration
            )
        }
    }
}