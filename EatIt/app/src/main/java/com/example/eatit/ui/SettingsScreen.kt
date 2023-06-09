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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eatit.R
import com.example.eatit.model.User
import com.example.eatit.ui.components.BackgroundImage
import com.example.eatit.ui.components.EatItButton
import com.example.eatit.utilities.createImageFile
import com.example.eatit.utilities.saveImage
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
    var user: User = usersViewModel.user!!
    val name = remember { mutableStateOf(user.name) }
    var address by rememberSaveable { usersViewModel.position }
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
            OutlinedTextField(
                value = name.value,
                onValueChange = { newText -> name.value = newText },
                label = { Text("Name") },
                trailingIcon={
                    EatItButton(text = "save", function = {
                        showDialog.value = true
                        focusManager.clearFocus()
                        usersViewModel.setName(name.value)
                        changedThing.value = "username"
                    })
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { newText -> address = newText },
                label = { Text("Address") },
                trailingIcon={
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Localized",
                        Modifier
                            .clickable(onClick = {
                                showDialog.value = true
                                focusManager.clearFocus()
                                startLocationUpdates()
                                changedThing.value = "position"
                            })
                    )
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            EatItButton(text = "Change Profile Picture", function = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }, icon = Icons.Filled.PhotoCamera)
            if (capturedImageUri.path?.isNotEmpty() == true) {
                LaunchedEffect(Unit) {
                    usersViewModel.setPhoto(usersViewModel.uploadPhoto(saveImage(context.applicationContext.contentResolver, capturedImageUri)!!).toString())
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            EatItButton(text = "Change password", function = {
                showChangedPsw.value = true
                usersViewModel.changePsw()
            })
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
            EatItButton(text = "Logout", function = {
                Firebase.auth.signOut()
                onNextButtonClicked()
            })
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Settings App:",
                fontSize = 30.sp,
                fontWeight = Bold,
            )
            Spacer(modifier = Modifier.size(10.dp))
            Row {
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