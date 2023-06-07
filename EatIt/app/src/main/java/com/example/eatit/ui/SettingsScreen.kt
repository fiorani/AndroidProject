package com.example.eatit.ui

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.eatit.R
import com.example.eatit.model.User
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.utilities.createImageFile
import com.example.eatit.viewModel.SettingsViewModel
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNextButtonClicked: () -> Unit,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit,
    sharedPref: SharedPreferences,
    theme: String?,
    onThemeChanged: (String?) -> Unit
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
            var user: User by remember { mutableStateOf(User()) }
            val textState = remember { mutableStateOf(user.name.toString()) }
            var city by rememberSaveable { usersViewModel.userPosition }
            var showDialog = remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            var changedThing = remember { mutableStateOf("") }
            var showChangedPsw = remember { mutableStateOf(false) }

            val light = LocalContext.current.getString(R.string.light_theme)
            val dark = LocalContext.current.getString(R.string.dark_theme)
            LaunchedEffect(Unit) {
                user = usersViewModel.getUser()
            }
            LaunchedEffect(user) {
                textState.value = user.name.toString()
            }
            Row {
                Text(
                    text="Settings:",
                    fontSize = 36.sp,
                    fontWeight = Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Row {
                OutlinedTextField(
                    value = textState.value,
                    onValueChange = { newText -> textState.value = newText },
                    label = { Text("Username") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Button(onClick= {
                    showDialog.value = true
                    focusManager.clearFocus()
                    usersViewModel.setName(textState.value)
                    changedThing.value = "username"
                }) {
                    Text("Ok")
                }
            }
            Spacer(modifier = Modifier.size(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { newText ->
                        city = newText
                    },
                    label = { Text("Place") },
                    modifier = Modifier.weight(4f)
                )
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Localized",
                    Modifier
                        .weight(1f)
                        .clickable(onClick = {
                            showDialog.value = true
                            focusManager.clearFocus()
                            startLocationUpdates()
                            changedThing.value = "position"
                        })
                )
            }
            Spacer(modifier = Modifier.size(70.dp))
            //--------------------------------------------------------------------------------------
            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )
            var capturedImageUri by remember {
                mutableStateOf<Uri>(Uri.EMPTY)
            }
            val cameraLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                    if (isSuccess) {
                        capturedImageUri = uri
                        usersViewModel.setPhoto(if(capturedImageUri.path == null) "" else capturedImageUri.path!!)
                        showDialog.value = true
                        changedThing.value = "profile photo"
                    }
                }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (it) {
                    cameraLauncher.launch(uri)
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            Button(
                onClick = {
                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = "Camera icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Change Profile Picture")
            }
            //--------------------------------------------------------------------------------------

            Button(
                onClick = {
                    showChangedPsw.value = true
                    usersViewModel.changePsw()
                },
            ) {
                Text("Change password")
            }
            if (showChangedPsw.value) {
                AlertDialog(
                    onDismissRequest = {
                        showChangedPsw.value = false
                    },
                    title = { Text("Modify password") },
                    text = { Text("You will receive an email shortly to reset your password. " +
                            "Follow the instructions in the email to complete the process.\n\n" +
                            "Make sure to check your spam folder in your email!") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showChangedPsw.value = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val tmp = if (theme == dark) light else dark
                    onThemeChanged(tmp)
                    with(sharedPref.edit()) {
                        putString("THEME_KEY", tmp)
                        apply()
                    }
                }
            ) {
                Text(
                    text = "Switch to ${theme?.lowercase()} mode"
                )
            }

            if (showDialog.value) {
                showAlertDialog(showDialog = showDialog, changedThing = changedThing.value)
            }

            Spacer(modifier = Modifier.size(15.dp))

            Button(
                onClick = {
                    Firebase.auth.signOut()
                    onNextButtonClicked()
                }
            ) {
                Text(
                    text = "Logout",
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
fun showAlertDialog(showDialog:  MutableState<Boolean>, changedThing: String)
{
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = { Text("Modify user") },
        text = { Text("Your $changedThing has been changed.") },
        confirmButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                }
            ) {
                Text("OK")
            }
        }
    )
}