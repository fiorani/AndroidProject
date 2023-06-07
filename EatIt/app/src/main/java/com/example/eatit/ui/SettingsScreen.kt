package com.example.eatit.ui

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.eatit.R
import com.example.eatit.model.User
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.utilities.createImageFile
import com.example.eatit.viewModel.UsersViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Objects

@Composable
fun SettingsScreen(
    onNextButtonClicked: () -> Unit,
    usersViewModel: UsersViewModel,
    startLocationUpdates: () -> Unit,
    sharedPref: SharedPreferences,
    theme: String?,
    onThemeChanged: (String?) -> Unit
) {
    var user: User by remember { mutableStateOf(User()) }
    val name = remember { mutableStateOf(user.name.toString()) }
    var address by rememberSaveable { usersViewModel.userPosition }
    val showDialog = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val changedThing = remember { mutableStateOf("") }
    val showChangedPsw = remember { mutableStateOf(false) }
    val themeChanged = remember { mutableStateOf(false) }
    val light = LocalContext.current.getString(R.string.light_theme)
    val dark = LocalContext.current.getString(R.string.dark_theme)
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
                usersViewModel.setPhoto(if (capturedImageUri.path == null) "" else capturedImageUri.path!!)
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
    LaunchedEffect(Unit) {
        user = usersViewModel.getUser()
    }
    LaunchedEffect(user) {
        name.value = user.name.toString()
    }
    Scaffold { paddingValues ->
        BackgroundImage(alpha = 0.1f)
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Settings Profile:",
                fontSize = 30.sp,
                fontWeight = Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { newText -> name.value = newText },
                    label = { Text("Name") },
                    modifier = Modifier.weight(4f)
                )
                Button(onClick = {
                    showDialog.value = true
                    focusManager.clearFocus()
                    usersViewModel.setName(name.value)
                    changedThing.value = "username"
                }, modifier = Modifier.weight(1f)) {
                    Text("save")
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { newText -> address = newText },
                    label = { Text("Address") },
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
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.width(300.dp),
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
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.width(300.dp),
                onClick = {
                    showChangedPsw.value = true
                    usersViewModel.changePsw()
                },
            ) {
                Text(text= "Change password")
            }
            if (showChangedPsw.value) {
                AlertDialog(
                    onDismissRequest = {
                        showChangedPsw.value = false
                    },
                    title = { Text("Modify password") },
                    text = {
                        Text(
                            "You will receive an email shortly to reset your password. " +
                                    "Follow the instructions in the email to complete the process.\n\n" +
                                    "Make sure to check your spam folder in your email!"
                        )
                    },
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
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.width(300.dp),
                onClick = {
                    Firebase.auth.signOut()
                    onNextButtonClicked()
                }
            ) {
                Text(
                    text = "Logout",
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Settings App:",
                fontSize = 30.sp,
                fontWeight = Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Row(){
                Text(
                    text = "Dark Theme: ",
                    fontWeight = Bold,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    modifier = Modifier.semantics { contentDescription = "Demo" },
                    checked = theme == dark,
                    onCheckedChange = {
                        val tmp = if (it) dark else light
                        onThemeChanged(tmp)
                        with(sharedPref.edit()) {
                            putString("THEME_KEY", tmp)
                            apply()
                            themeChanged.value = true
                        }
                    })
            }

            if (showDialog.value) {
                ShowAlertDialog(showDialog = showDialog, changedThing = changedThing.value)
            }
            if (themeChanged.value) {
                themeChanged.value = false
                (LocalContext.current as? Activity)?.recreate()
            }
        }
    }
}

@Composable
fun ShowAlertDialog(showDialog: MutableState<Boolean>, changedThing: String) {
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